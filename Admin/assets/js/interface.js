const split = '|';
// const preFix = "http://rap2api.taobao.org/app/mock/234350/api/v1/";
const preFix = "http://139.199.8.103:8888/api/v1/"

const user = preFix + "user/";
const register = user + "register";
const login = user + "login";
const updateToken = user + "refresh_token";
const userList = user + "all_info";
const updateUser = user + "update";


const admin = preFix + "administration/";
// 省份数据获取接口
const province = admin + "province";
// 城市数据
const city = admin + "city";
// 街道数据获取接口
const street = admin + "street";
// 行政区数据获取接口
const district = admin + "district";
// 社区数据获取接口
const community = admin + "community";
// 小区数据获取接口
const garden = admin + "garden";
// excel导入数据接口
const fileUpload = admin + "import_data";


const data = preFix + "data/";
// 图片相关接口
const picture = preFix + "data/";
// 获取建筑类别接口
// 返回已有建筑的种类名称和对应的id
const building = data + "building";
// 获取地图标记数据
const map = data + "map";
// 获取小区列表
const gardenList = data + "garden";
// 获取小区图片种类
const gardenPictureKindList = picture + "garden_picture_kind";
// 楼栋图片种类获取接口
const buildingPictureKindList = picture + "building_picture_kind";
// 修改小区概况表接口
const garden_base_info = data + "garden_base_info";
// 修改楼栋概况表接口
const building_base_info = data + "building_base_info";


const BAIDU_MAP = 1;
const TENCENT_MAP = 2;
const GOOGLE_MAP = 3;
// 地图数据获取模块
const MapData = preFix + "get_data/";
const getMapData = MapData + "map";

const excelData = preFix + "get_data/";
const tableColumn = "column";
const tableData = "data";
const table = [excelData + "garden_base_info", excelData + "building_base_info",
    "", ""];
const excelTable = [excelData + "garden_base_table", excelData + "garden_table", excelData + "building_base_table", excelData + "building_table"];
var token;
var userLists;

// 地图
function getMapDataFun(gardenId, mapId) {
    var result;
    xhrPost(getMapData, conJson(conSplit("token", getCookie("token")),
        conSplit("gardenId", gardenId),
        conSplit("mapId", mapId)), false)
        .done(function (res) {
            result = res.data.map_data;
        });
    return result;
}

// 行政区
function getProvince() {
    var result;
    xhrPost(province, conJson(conSplit("token", getCookie("token"))), false)
        .done(function (res) {
            result = res.data.provinces;
        });
    return result;
}

function getCity(provinceId) {
    var result;
    xhrPost(city, conJson(conSplit("token", getCookie("token")),
        conSplit("provinceId", provinceId)), false)
        .done(function (res) {
            result = res.data.cities;
        });
    return result;
}

function getDistrict(cityId) {
    var result;
    xhrPost(district, conJson(conSplit("token", getCookie("token")),
        conSplit("cityId", cityId)), false)
        .done(function (res) {
            result = res.data.districts;
        });
    return result;
}

function getStreet(districtId) {
    var result;
    xhrPost(street, conJson(conSplit("token", getCookie("token")),
        conSplit("districtId", districtId)), false)
        .done(function (res) {
            result = res.data.streets;
        });
    return result;
}

function getCommunity(streetId) {
    var result;
    xhrPost(community, conJson(conSplit("token", getCookie("token")),
        conSplit("streetId", streetId)), false)
        .done(function (res) {
            result = res.data.communities;
        });
    return result;
}

function conSplit() {
    return arguments[0] + split + arguments[1];
}

function conJson() {
    // var datas = [];
    var data = {};
    for (var i = arguments.length - 1; i >= 0; i--) {
        strs = arguments[i].split(split);
        data[strs[0]] = strs[1];
    }
    // datas.push(data);
    return JSON.stringify(data);
}

function xhrPost(url, data, async = true) {
    return $.ajax({
        url: url,
        method: "POST",
        dataType: "json",
        async: async,
        contentType: 'application/json',
        data: data
    });
}

function loginFun(username, password) {
    xhrPost(login, conJson(conSplit("iemi", username), conSplit("password", password)))
        .done(function (data) {
            if (data.code == 0) {
                this.token = data.data.token;
                setCookie("username", username);
                addCookie("token", data.data.token);
                md.showNotification("bottom", "right", "登录成功");
            } else {
                md.showNotification("bottom", "right", "登录失败，请重试", "danger");
            }
            initUrl();
        });
}

// TODO: add hidden input of token in footer
function updateTokenFun(token = '') {

    xhrPost(updateToken, conJson(conSplit("token", token)))
        .done(function (data) {
            this.token = data.data.token;
        });
}

function getUserLists() {
    var token = getCookie("token");
    xhrPost(userList, conJson(conSplit("token", token)), false)
        .done(function (para) {
            userLists = para.data;
        });
    return userLists;
}

function updateUserFun(data) {
    for (let index in data) {
        console.log(data[index]);
        var json = {
            "newUsers": [
                data[index]
            ]
        };
        xhrPost(register, JSON.stringify(json))
            .done(function (data) {
                if (data.code == 0) {
                    md.showNotification("bottom", 'right', '更新成功');
                } else {
                    md.showNotification("bottom", 'right', '更新失败，请联系管理员', 'danger');
                }
            }).fail(function (code) {
            md.showNotification("bottom", 'right', '更新失败，请联系管理员', 'danger');
        });
    }
    ;

}

// 获取某个社区communityId下面的所有小区的表
function getExcelData(tableId, idName, communityId) {
    // 获取数据
    var result = [];
    var gardenLists = getGardenListOfCommunity(communityId);
    // console.log(gardenLists)
    for (var p in gardenLists) {
        result[p] = getSingleGardenExcelData(tableId, idName, gardenLists[p].id)
        result[p].name = gardenLists[p].name;
    }
    return result;
}

function getGardenListOfCommunity(communityId) {
    var result;
    xhrPost(garden, conJson(conSplit("token", getCookie("token")),
        conSplit("communityId", communityId)), false)
        .done(function (res) {
            result = res.data.gardens;
        });
    return result;
}

function getSingleGardenExcelData(tableId, idName, gardenId) {
    var result;
    xhrPost(table[tableId - 1], conJson(conSplit("token", getCookie("token")),
        conSplit(idName, gardenId)), false)
        .done(function (res) {
            result = res.data;
        });
    return result;
}

// TODO: 修改表头链接
function getExcelHeader(tableId, idName, communityId) {
    // 获取表头
    var result;
    xhrPost(table[tableId - 1], conJson(conSplit("token", getCookie("token")),
        conSplit(idName, communityId)))
        .done(function (res) {
            result = res.data;
        });

    return result;
}

function userUploadFun() {
    uploadFun("userUpload");
}

function adminUpdateFun() {
    uploadFun("administrationUpload");
}

function uploadFun(id) {
    var formData = new FormData();
    formData.append("token", getCookie("token"));
    formData.append("file", document.getElementById(id).files[0]);
    $.ajax({
        url: fileUpload,
        method: "POST",
        cache: false,
        data: formData,
        processData: false,
        contentType: false
    })
        .done(function (res) {
            md.showNotification("bottom", "right", res.message);
        })
        .fail(function (res) {
            md.showNotification("bottom", "right", res.message, "danger");
        });
}

function setCookie(cname, cvalue, exdays = 1) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

function addCookie(cname, cvalue) {
    setCookie(cname, cvalue);
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function initUrl() {
    if (getCookie("token") == ""
        || getCookie("username") == "") {
        window.location.href = "login.html";
    } else {
        window.location.href = "dashboard.html";
    }
}

function logout() {
    setCookie("token", "", -1);
    initUrl();
}


function debugOutput(arg) {
    console.log(arg);
}

function downloadExcel() {
    var item = $(this);
    var gardenId = item.data('gardenId');
    var tableId = item.data('table-id');
    var url = excelTable[tableId - 1];
    var $form = $("<form>"); //定义一个form表单
    $form.hide().attr({target: '', method: 'post', 'action': url});
    var $token = $("<input>");
    $token.attr({"type": "hidden", "name": 'token'}).val(getCookie("token"));
    $form.append($token);
    var $gardenId = $("<input>");
    $gardenId.attr({"type": "hidden", "name": 'gardenId'}).val(gardenId);
    $form.append($gardenId);

    $form.appendTo($("body")).submit().remove();
}