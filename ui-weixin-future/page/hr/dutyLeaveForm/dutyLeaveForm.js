var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    formData: {},
    formProperty: {},
    response: {},
    submitDisabled: false,
    leaveDisabled: false,
    options: null
  },

  onLoad: function (options) {
    var that = this;
    that.data.options = options
    app.autoLogin(function () {
      that.initPage()
    })
  },

  initPage: function () {
    var that = this;
    wx.request({
      url: $util.getUrl("basic/hr/dutyLeave/getQuery"),
      data: {},
      method: 'GET',
      header: {
        Cookie: "JSESSIONID=" + app.globalData.sessionId
      },
      success: function (res) {
        that.setData({ formProperty: res.data.extra })
      }
    })
  },

  bindDateChange: function (e) {
    var that = this;
    var name = e.currentTarget.dataset.name;
    if (name == 'dutyDateStart') {
      that.setData({ "formData.dutyDateStart": e.detail.value });
      that.dateIsEqual();
    } else {
      that.setData({ "formData.dutyDateEnd": e.detail.value });
      that.dateIsEqual();
    }
  },
  bindTypeChange: function (e) {
    var that = this;
    var name = e.currentTarget.dataset.name;
    if (name == 'dateType') {
      that.setData({
        'formData.dateType': that.data.formProperty.dateList[e.detail.value],
      })
    } else {
      that.setData({
        'formData.leaveType': that.data.formProperty.leaveList[e.detail.value]
      });

    }
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ submitDisabled: true });
    wx.request({
      url: $util.getUrl("basic/hr/dutyLeave/save"),
      data: e.detail.value,
      header: {
        Cookie: "JSESSIONID=" + app.globalData.sessionId
      },
      success: function (res) {
        console.log(res.data)
        if (res.data.success) {
          wx.navigateBack();
        } else {
          that.setData({ "response.error": res.data.message,"response.data": res.data.extra.errors, submitDisabled: false})
        }
      }
    })
  },
  showError: function (e) {
    var that = this;
    var key = e.currentTarget.dataset.key;
    var responseData = that.data.response.data;
    if (responseData && responseData[key] != null) {
      that.setData({ "response.error": responseData[key].message });
      delete responseData[key];
      that.setData({ "response.data": responseData })
    } else {
      that.setData({ "response.error": '' })
    }
  },
  dateIsEqual: function () {
    var that = this;
    var dutyDateStart = that.data.formData.dutyDateStart;
    var dutyDateEnd = that.data.formData.dutyDateEnd;
    if ($util.isNotBlank(dutyDateStart) && $util.isNotBlank(dutyDateEnd)) {
      that.setData({
        'formData.dateType': (dutyDateStart == dutyDateEnd ? '' : '全天'),
        leaveDisabled: (dutyDateStart == dutyDateEnd ? false : true)
      })
    }
  }
})