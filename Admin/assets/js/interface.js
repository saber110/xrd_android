
const split = '|';
// const preFix = "http://rap2api.taobao.org/app/mock/234350/api/v1/";
const preFix = "http://139.199.8.103:8888/api/v1/"

const user = preFix + "user/";
const register = user + "register";
const login = user + "login";
const updateToken = user + "refresh_token";
const userList = user + "lists";
const updateUser = user + "update";


const admin = preFix + "administration/";
// 街道数据获取接口
const street = admin + "street";
// 行政区数据获取接口
const district = admin + "district"; 
// 社区数据获取接口
const community = admin + "community"; 


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

var token;
var userLists;

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

function xhrPost(url, data) {
	console.log(data);
	return $.ajax({
		url: url,
		method: "POST",
		dataType: "json",
		contentType:'application/json',
		data: data
	});
}

function loginFun(username, password) {
	xhrPost(login , conJson(conSplit("iemi", username), conSplit("password", password)))
		.done(function (data) {
			if(data.code == 0){
				md.showNotification("bottom","right","登录成功");
				this.token = data.data.token;
			}
			else{
				md.showNotification("bottom","right","登录失败，请重试",danger);
			}
		});
}

// TODO: add hidden input of token in footer
function updateTokenFun(token = '') {

	xhrPost(updateToken, conJson(conSplit("token", token)))
		.done(function (data) {
			this.token = data.data.token;
		});
}

function getUserLists(token = '') {

	xhrPost(userList,conJson(conSplit("token", token)))
		.done(function (data) {
			// TODO: 更新列表
			this.userLists = data;
		});
}

function updateUser(data) {
	for(let index in data){
		console.log(data[index]);
		var json ={
			"newUsers" : [
				data[index]
				]
		};
		xhrPost(register, JSON.stringify(json))
			.done(function (data) {
				if (data.code == 0) {
				  	md.showNotification("bottom",'right','更新成功');
				}
				else{
					md.showNotification("bottom",'right','更新失败，请联系管理员','danger');
				}
			}).fail(function (code) {
				md.showNotification("bottom",'right','更新失败，请联系管理员','danger');
			});
	};
	
}















function debugOutput(argument) {
	console.log(argument);
}