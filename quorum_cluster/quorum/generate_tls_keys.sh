set -e
#
chmod 600 /data/root_ca.crt
chmod 600 /data/root_ca.key

mkdir -p /usr/local/share/ca-certificates/extra
cp /data/root_ca.crt /usr/local/share/ca-certificates/extra/root_ca.crt

ls -ltr /usr/local/share/ca-certificates/extra/

cp /data/root_ca.crt ${QUORUM_DIR}/

echo "extra/root_ca.crt" >> /etc/ca-certificates.conf

update-ca-certificates

# creating key for tls server
openssl genrsa -out ${QUORUM_DIR}/tls_server.key 2048 > /dev/null
openssl req -new -sha512 -key ${QUORUM_DIR}/tls_server.key -subj "/CN=${DOMAIN_NAME}" -out ${QUORUM_DIR}/tls_server.csr
openssl x509 -req -in ${QUORUM_DIR}/tls_server.csr -CA /data/root_ca.crt -CAkey /data/root_ca.key -CAcreateserial -out ${QUORUM_DIR}/tls_server.crt -days 1024 -sha512 -extfile /data/v3.ext
chmod 600 ${QUORUM_DIR}/tls_server.crt
chmod 400 ${QUORUM_DIR}/tls_server.key

openssl x509 -outform PEM -in ${QUORUM_DIR}/tls_server.crt -out ${QUORUM_DIR}/tls-server-cert.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -in ${QUORUM_DIR}/tls_server.key -out ${QUORUM_DIR}/tls-server-key.pem -nocrypt

# creating key for tls client
openssl genrsa -out ${QUORUM_DIR}/tls_client.key 2048 > /dev/null
openssl req -new -sha512 -key ${QUORUM_DIR}/tls_client.key -subj "/CN=${DOMAIN_NAME}" -out ${QUORUM_DIR}/tls_client.csr
openssl x509 -req -in ${QUORUM_DIR}/tls_client.csr -CA /data/root_ca.crt -CAkey /data/root_ca.key -CAcreateserial -out ${QUORUM_DIR}/tls_client.crt -days 1024 -sha512 -extfile /data/v3.ext
chmod 600 ${QUORUM_DIR}/tls_client.crt
chmod 400 ${QUORUM_DIR}/tls_client.key

openssl x509 -outform PEM -in ${QUORUM_DIR}/tls_client.crt -out ${QUORUM_DIR}/tls-client-cert.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -in ${QUORUM_DIR}/tls_client.key -out ${QUORUM_DIR}/tls-client-key.pem -nocrypt

