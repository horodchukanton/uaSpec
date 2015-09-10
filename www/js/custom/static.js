/**
 * Created by User on 016 16.05.15.
 */

getData('header');
getData('footer');


function getData(name) {
    //console.log(url+ name + '=get');
    var result;
    $.get("/" + name+".html", function (data) {
            $(name).html(data);
        },
        'text');
    return result;
}

