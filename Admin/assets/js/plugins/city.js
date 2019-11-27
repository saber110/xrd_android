var provinceSelect = document.getElementById('province');
var citySelect = document.getElementById('city');
var districtSelect = document.getElementById('district');
var streetSelect = document.getElementById('street');
var communitySelect = document.getElementById('community');

document.addEventListener('DOMContentLoaded', function() {
  var provinces = getProvince();
  for(var p in provinces){
    var option = document.createElement("option");
    option.value = provinces[p].id;
    option.innerHTML = provinces[p].name;
    provinceSelect.appendChild(option);
  }
});

provinceSelect.onchange = function() {
  // 获取省份的值
  var provinceId = provinceSelect.options[provinceSelect.options.selectedIndex].value;
  var cities = getCity(provinceId);
  for(var p in cities){
    var option = document.createElement("option");
    option.value = cities[p].id;
    option.innerHTML = cities[p].name;
    citySelect.appendChild(option);
  }
};
citySelect.onchange = function() {
  // 获取城市的值
  var cityId = citySelect.options[citySelect.options.selectedIndex].value;
  var districts = getDistrict(cityId);
  for(var p in districts){
    var option = document.createElement("option");
    option.value = districts[p].id;
    option.innerHTML = districts[p].name;
    districtSelect.appendChild(option);
  }
};
districtSelect.onchange = function() {
  var districtId = districtSelect.options[districtSelect.options.selectedIndex].value;
  var streets = getStreet(districtId);
  for(var p in streets){
    var option = document.createElement("option");
    option.value = streets[p].id;
    option.innerHTML = streets[p].name;
    streetSelect.appendChild(option);
  }
};
streetSelect.onchange = function() {
  var streetId = streetSelect.options[streetSelect.options.selectedIndex].value;
  var communities = getCommunity(streetId);
  for(var p in communities){
    var option = document.createElement("option");
    option.value = communities[p].id;
    option.innerHTML = communities[p].name;
    communitySelect.appendChild(option);
  }
};