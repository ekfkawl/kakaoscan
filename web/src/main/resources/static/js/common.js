function request(url, param, method) {
    var result;
    $.ajax({
        url: url,
        type: method,
        data: JSON.stringify(param),
        dataType: 'json',
        async: false,
        contentType:"application/json",
        success: function(res) {
            result = res;
        },
        error: function(res) {
            result = res;
        }
    });
    return result;
}

function _get(url, param) {
    return request(url, param, 'GET');
}

function _post(url, param) {
    return request(url, param, 'POST');
}

function _put(url, param) {
    return request(url, param, 'PUT');
}

function _delete(url, param) {
    return request(url, param, 'DELETE');
}
