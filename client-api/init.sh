#!/usr/bin/env bash

if [ $? -eq 1 ]
then
  QUORUM_DNS=127.0.0.1
fi

sns_alarm_failure() {
    if [ $ENABLE_SNS_EMAIL_ALERTS = true ]; then
        aws sns publish --topic-arn "$SNS_TOPIC_ARN" --subject "$1" --message "$2" --region "$REGION"
    fi
}

if ! [ "$IS_LOCAL" == true ]; then
  deploymentIdentifier=`echo "$S3_CONFIG_PATH" | cut -d'/' -f1 | cut -d'-' -f3-`
  message=$(aws s3 cp s3://"$S3_CONFIG_PATH" /app/api_config.json 2>&1)
  if [ "$?" != 0 ]; then
     echo $message
     sns_alarm_failure "[$deploymentIdentifier] [Api]: Configuration download failed" "$message on $S3_CONFIG_PATH"
     exit 1
  fi

  keys=`jq 'keys[]' api_config.json 2>&1`
  if [ "$?" != 0 ] || [ "$keys" = "" ]; then
     echo $keys
     msg=$([ "$keys" = "" ] && echo "Config file is empty" || echo "$keys")
     sns_alarm_failure "[$deploymentIdentifier] [Api]: Configuration parsing failed" "$msg on $S3_CONFIG_PATH"
     exit 1
  fi

  set -e

  for key in ${keys}
  do
      key_uppercase=`echo $key | awk '{ print toupper($0) }' | tr -d '"'`
      value=`jq -r .$key api_config.json`
      export ${key_uppercase}=$value
  done

  smtp_password=`aws ssm get-parameter --name "$SMTP_PASSWORD_PATH" --with-decryption --query "Parameter.Value" --output text`
  export SMTP_PASSWORD=$smtp_password

  aws s3 cp s3://"$COMPANIES_INFO_PATH" companies_info.json
  export COMPANIES_INFO=`cat companies_info.json`
  rm -f  companies_info.json
  export TENANTS=$(python3 /app/fetch_entities_password.py 2>&1)

  export KOMGO_KEY_REGISTRY_PATH=`echo $TENANTS | jq -r '.[].komgo_keyregistry'`

  for path in $(echo $KOMGO_KEY_REGISTRY_PATH); do
     echo "Downloading s3://${path}..."
     aws s3 cp "s3://$path" komgo_key.json
     LOCAL_PATH=$(echo $path | sed 's/\/komgo_key.*//g')
     echo "Moving file to $LOCAL_PATH"
     mkdir -p "$LOCAL_PATH" && mv komgo_key.json "$LOCAL_PATH"
   done
fi

set -e

contracts_folder="contracts"

local_absolute_contract_registry_path="/tmp/contract_registry.json"
remote_relative_contract_registry_path="$contracts_folder/R${CONTRACT_RELEASE_VERSION}_ContractRegistry.json"

source ./contract_registry_address.sh

export CONTRACT_REGISTRY_ADDRESS=`jq -r .ContractRegistry.address $local_absolute_contract_registry_path`
if [[ "$CONTRACT_REGISTRY_ADDRESS" == "" ]]; then
  echo "contract registry address not found in $local_absolute_contract_registry_path. Exiting API startup"
  exit 1
else
  echo "contract registry address found: $CONTRACT_REGISTRY_ADDRESS"
fi

if [ "$IS_LOCAL" == true ]; then
    java -Djavax.net.ssl.trustStore="$LOCAL_SSL_STORE_PATH" -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:7654 -Djavax.net.ssl.trustStorePassword="$LOCAL_SSL_STORE_PASSWORD" -jar vakt-api.jar --web3.rpc.url=http://${QUORUM_DNS}:22000
   else
    java -jar vakt-api.jar --web3.rpc.url=http://${QUORUM_DNS}:22000
fi

