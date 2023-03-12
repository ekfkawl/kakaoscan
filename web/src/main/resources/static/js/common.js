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

function decodeString(s) {
    const regex = /\\u([\dA-Fa-f]{4})|\\n/g;
    const result = s.replace(regex, (match, group) => {
        if (group) {
            return String.fromCodePoint(parseInt(group, 16));
        } else {
            return '';
        }
    }).replace(/\?\?/g, ''); // ?? -> ''
    return result;
}

function render($ele, res, k, ext, className) {
    $ele.empty();
    if (res.hasOwnProperty(k)) {
        res[k].sort(function (a, b) {
            return Number(a.Name) - Number(b.Name);
        });

        for (let i in res[k]) {
            let url = res['Host'] + res[k][i].Dir + '/' + res[k][i].Name + ext + '?t=' + Math.random();

            switch (ext) {
                case '.mp4.jpg':
                    $ele.append(`
                                 <div class="${className}">
                                    <img
                                            src=${url}
                                            data-mdb-img=${url}
                                            alt=""
                                            class="w-100 shadow-1-strong rounded"
                                    />
                                </div>
                            `)
                    break;

                case '.mp4':
                    $ele.append(`
                                <div class="${className}">
                                    <iframe
                                            src=${url}
                                            data-mdb-img=${url}
                                            allowfullscreen
                                    ></iframe>
                                </div>
                            `)
                    break;
            }
        }
    }
}