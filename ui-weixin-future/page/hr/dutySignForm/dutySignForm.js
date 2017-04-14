var $util = require("../../../util/util.js");
var app = getApp();
Page({
  data: {
    formData: {},
    formProperty: {},
    response: {},
    submitDisabled: false,
    submitHidden: false,
    options: null
  },
  onLoad: function (options) {
    var that = this;
    that.data.options = options;
    app.autoLogin(function () {
      that.initPage()
    });
  },
  initPage: function () {
    var that = this;
    that.getLocation();
    if (that.data.options.action == 'add') {
      that.setData({ "formData.dutyDateTime": $util.formatLocalDateTime(new Date()) });
      wx.showToast({
        title: '加载中',
        icon: 'loading',
        duration: 10000
      });
    } else {
      wx.request({
        url: $util.getUrl("basic/hr/dutySign/detail"),
        data: { id: that.data.options.id },
        header: {
          'x-auth-token': app.globalData.sessionId,
          'authorization': "Bearer" + wx.getStorageSync('token').access_token
        },
        success: function (res) {
          that.setData({ formData: res.data });
          that.setData({ "submitHidden": true });
          var images = new Array();
          images.push({
            id: res.data.attachment,
            preview: $util.getUrl('basic/sys/folderFile/preview?x-auth-token=' + app.globalData.sessionId +'authorization=Bearer' + wx.getStorageSync('token').access_token + '&id=' + res.data.attachment),
            view: $util.getUrl('basic/sys/folderFile/view?x-auth-token=' + app.globalData.sessionId +'authorization=Bearer' + wx.getStorageSync('token').access_token +"&id=" + res.data.attachment)
          })
          that.setData({ "formProperty.images": images })
        }
      })
    }
  },
  getLocation: function (e) {
    var that = this;
    wx.getLocation({
      type: 'wgs84',
      success: function (res) {
        console.log(res)
        that.setData({ "formData.longitude": res.longitude, "formData.latitude": res.latitude, "formData.accuracy": res.accuracy })
        wx.getNetworkType({
          success: function (res) {
            that.setData({ "formData.networkType": res.networkType })
          }
        })
        wx.getSystemInfo({
          success: function (res) {
            that.setData({ "formData.model": res.model })
          }
        })
        wx.request({
          url: $util.getUrl("basic/api/map/getPoiList?longitude=" + res.longitude + "&latitude=" + res.latitude),
          method: 'GET',
          header: { 'x-auth-token': app.globalData.sessionId,
                    'authorization':'Bearer'+wx.getStorageSync('token').access_token 
           },
          success: function (res) {
            that.setData({ "formProperty.addressList": res.data });
            wx.hideToast();
          },
          fail: function () {
            console.log(res);
          }
        });
      }
    })
  },
  bindAddressChange: function (e) {
    this.setData({
      "formProperty.addressIndex": e.detail.value
    });
  },
  addImage: function (e) {
    var that = this;
    var images = [];
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['camera'],
      success: function (res) {
        console.log(res);
        var tempFilePaths = res.tempFilePaths
        wx.uploadFile({
          url: $util.getUrl('basic/sys/folderFile/upload'),
          header: {
            'x-auth-token': app.globalData.sessionId,
            'authorization':'Bearer' + wx.getStorageSync('token').access_token 
          },
          filePath: tempFilePaths[0],
          name: 'file',
          formData: {
            uploadPath: 'dutySign'
          },
          success: function (res) {
            console.log(res);
            var folderFile = JSON.parse(res.data)[0];
            images.push({
              id: folderFile.id,
              preview: $util.getUrl('basic/sys/folderFile/preview?x-auth-token=' + app.globalData.sessionId + 'authorization=Bearer' + wx.getStorageSync('token').access_token + '&id=' + folderFile.id),
              view: $util.getUrl('basic/sys/folderFile/view?x-auth-token=' + app.globalData.sessionId + 'authorization=Bearer' + wx.getStorageSync('token').access_token + '&id=' + folderFile.id)
            })
            that.setData({ "formProperty.images": images })
          }
        })
      }
    })
  },
  showImageActionSheet: function (e) {
    var that = this;
    var index = e.target.dataset.index;
    var itemList = ['预览', '删除'];
    wx.showActionSheet({
      itemList: itemList,
      success: function (res) {
        if (!res.cancel) {
          if (itemList[res.tapIndex] == '预览') {
            wx.previewImage({
              current: that.data.formProperty.images[index].view, // 当前显示图片的http链接
              urls: [that.data.formProperty.images[0].view]
            })
          } else {
            that.data.formProperty.images.splice(index, 1);
            that.setData({ "formProperty.images": that.data.formProperty.images });
          }
        }
      }
    });
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ submitDisabled: true });
    wx.request({
      url: $util.getUrl("basic/hr/dutySign/save"),
      data: e.detail.value,
      header: {
        'x-auth-token': app.globalData.sessionId,
        'authorization':'Bearer' + wx.getStorageSync('token').access_token 
      },
      success: function (res) {
        if (res.data.success) {
          wx.navigateBack();
        } else {
          console.log()
          that.setData({ "response.data": res.data, submitDisabled: false });
        }
      }
    })
  },
  showError: function (e) {
    var that = this;
    var key = e.currentTarget.dataset.key;
    var responseData = that.data.response.data;
    if (responseData && responseData.errors && responseData.errors[key] != null) {
      that.setData({ "response.error": responseData.errors[key].message });
      delete responseData.errors[key];
      that.setData({ "response.data": responseData })
    } else {
      that.setData({ "response.error": '' })
    }
  }
})