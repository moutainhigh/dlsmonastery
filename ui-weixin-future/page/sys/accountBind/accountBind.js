//获取应用实例
var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    userInfo: [],
    disabled: false,
    code: "",
  },
  onLoad: function () {
    var that = this
    app.getUserInfo(function (userInfo) {
      that.setData({
        userInfo: userInfo
      })
    });
    wx.login({
      success: function (res) {
        that.setData({
        code: res.code
      })
      }
    })
  },
  formSubmit: function (e) {
    var that = this;
    if (e.detail.value.loginName == "" || e.detail.value.password == "") {
      wx.showModal({
        content: "请输入用户名及密码",
        showCancel: false
      });
      return;
    }
    that.setData({ disabled: true });
    wx.request({
      url: $util.getUrl("basic/hr/accountWeixin/bind"),
      data: {
        loginName: e.detail.value.loginName,
        password: e.detail.value.password,
        code: that.data.code
      },
      header: {
        'x-auth-token': app.globalData.sessionId
      },
      success: function (res) {
        console.log(res)
        if (res.data.success) {
          wx.navigateBack({ delta: 10 })
        } else {
          wx.showModal({
            title: "绑定失败",
            content: res.data.message,
            showCancel: false,
            success: function (res) {
              if (res.confirm) {
                that.setData({ disabled: false });
              }
            }
          });
        }
      }
    })
  }
})