

// const preFix = "http://rap2api.taobao.org/app/mock/234350/api/v1/";
const preFix = "http://139.199.8.103:8888/api/v1/"

const user = preFix + "user/";
const register = user + "register";
const login = user + "login";
const updateToken = user + "refresh_token";
const userList = user + "lists";


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

function loginFun(username, password) {
	$.ajax({
		url: login,
		method: "POST",
		data:{
			"iemi": username,
			"password": password
		}
	}).done(function (data) {
		updateTokenFun();
		console.log(data);
	});
}

function updateTokenFun(token = '') {
	$.ajax({
		url: updateTocken,
		method: "POST",
		data:{
			"token": token
		}
	}).done(function (data) {
		this.token = token;
	})
}

function getUserLists(token = '') {
	$.ajax({
		url: userList,
		method: "POST",
		data:{
			"token": token
		}
	})
}