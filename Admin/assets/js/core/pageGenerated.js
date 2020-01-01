

// 获取js src后面参数
var scripts = document.getElementsByTagName('script');
// 只能获取到该js插入的前面的js列表
var src = scripts[scripts.length - 1].src;

var arg = src.indexOf('?') !== -1 ? src.split('?').pop() : '';
var settings = {};
arg.replace(/(\w+)(?:=([^&]*))?/g, function(a, key, value) {
  var valueStr = decodeURI(value);
  // 去掉前后引号
  settings[key] = valueStr.substr(1, valueStr.length - 2); 
});


fetch("./sidebar.html")
.then(response => {
  return response.text()
})
.then(data => {
  document.getElementById("sidebar").innerHTML = data;
});

fetch("./navbar.html")
.then(response => {
  return response.text()
})
.then(data => {
  document.getElementById("navbar").innerHTML = data;
})
.then(data => {
  document.getElementById("contentOutline").innerHTML = settings.contentOutline;
});

fetch("./footer.html")
.then(response => {
  return response.text()
})
.then(data => {
  document.getElementById("footer").innerHTML = data;
});



// 动态引入js
// fetch("../assets/js/core/jsLists.js")
// .then(response => {
//   return response.text()
// })
// .then(data => {
//   document.getElementById("corejs").innerHTML = data;
// });

// fetch("../assets/js/core/windowAnimation.js")
// .then(response => {
//   return response.text()
// })
// .then(data => {
//   document.getElementById("corejs").innerHTML += data;
// });
