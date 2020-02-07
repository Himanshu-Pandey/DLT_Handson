$(document).ready(function () {

    const boothId = getUrlVars()["boothId"] || 'booth1';

    $('#call-result-api').click(function (event) {
        $.get('http://localhost:8080/election-commission/results', (data) => {
            $(`<div class="alert alert-primary results" role="alert">
        ${data}
      </div>`).replaceAll($(".results"));
        });
    });

    $.get(`http://localhost:8080/election-commission/booth${boothId}/voters`, (data) => {
        data.forEach(element => {
            $(`<option value="${element.address}">${element.name}</option>`).appendTo("#ddlVoters");
        });
    });

    $('select').on('change', () => {
        let selectedVoter = $('#ddlCandidates :selected');
        let selectedCandidate = $('#ddlVoters :selected');
        $("#btnVote").attr('disabled', (selectedVoter.attr('value') == undefined || selectedCandidate.attr('value') == undefined));
    });

    $('#btnVote').click(() => {
        let selectedVoter = $('#ddlVoters :selected').text();
        let selectedCandidate = $('#ddlCandidates :selected').attr('value');

        $.post(`http://localhost:8080/booth/booth${boothId}/user/${selectedVoter}/vote/${selectedCandidate}`, function (data) {
            $(`<div class="alert alert-primary results" role="alert" style="word-break: break-word;"><pre>${data.split(",").join("\n")}</pre></div>`).replaceAll($(".results"));
        }).fail(function (data) {
            $(`<div class="alert alert-danger results" role="alert">
            <pre>${data.responseText.split(",").join("\n")}</pre>
          </div>`).replaceAll($(".results"));
        });
    });

    $('#lnkRefreshContract').click(() => {
        $.post(`http://localhost:8080/election-commission/refresh`, function (data) {
            alert('Refresh update :' + data);
        }).fail(function (data) {
            alert('Refresh update :' + data);
        });
    });

    $("#spnBoothId").text(boothId);
});

function getUrlVars() {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}