<template>
  <div>
    <head-tab active="shopBuildForm"></head-tab>
    <div >
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="140px" class="form input-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('shopBuildForm.shopId')" prop="shopId">
              <depot-select v-model="inputForm.shopId" category="shop" @input="refreshRecentMonthSaleAmount()" :disabled="shopDisabled"></depot-select>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.monthSaleQty')" prop="recentSaleDescription">
              {{recentSaleDescription}}
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.investInCause')" prop="investInCause">
              <el-input v-model="inputForm.investInCause" type="textarea"></el-input>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.shopType')" prop="shopType">
              <dict-enum-select v-model="inputForm.shopType" category="店面类型"></dict-enum-select>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.fixtureType')" prop="fixtureType">
              <dict-enum-select v-model="inputForm.fixtureType" category="装修类别" @input="shopChange"></dict-enum-select>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.fixtureContent')" prop="fixtureContent">{{fixtureContent}}
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.newContents')" prop="newContents">
              <el-input v-model="inputForm.newContents" ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('shopBuildForm.buildType')" prop="buildType">
              <dict-enum-select v-model="inputForm.buildType" category="项目建设方式"></dict-enum-select>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.applyAccount')" prop="applyAccountId">
              <account-select v-model="inputForm.applyAccountId"></account-select>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.content')" prop="content">
              <el-input v-model="inputForm.content" type="textarea"></el-input>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.remarks')" prop="remarks">
              <el-input v-model="inputForm.remarks" type="textarea"></el-input>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.shopAgreement')" prop="shopAgreement">
              <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/门店建设" :on-change="handleChange2" :on-remove="handleRemove2" :on-preview="handlePreview2" :file-list="fileList2" list-type="picture">
                <el-button size="small" type="primary">{{$t('shopBuildForm.clickUpload')}}</el-button>
                <div slot="tip" class="el-upload__tip">{{$t('shopBuildForm.uploadImageSizeFor5000KB')}}</div>
              </el-upload>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.scenePhoto')" prop="scenePhoto">
              <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/门店建设" :on-change="handleChange1" :on-remove="handleRemove1" :on-preview="handlePreview1" :file-list="fileList1" list-type="picture">
                <el-button size="small" type="primary">{{$t('shopBuildForm.clickUpload')}}</el-button>
                <div slot="tip" class="el-upload__tip">{{$t('shopBuildForm.uploadImageSizeFor5000KB')}}</div>
              </el-upload>
            </el-form-item>
            <el-form-item :label="$t('shopBuildForm.confirmPhoto')" prop="confirmPhoto" v-if="processStatus === '产品经理待确认'">
              <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/门店建设" :on-change="handleChange3" :on-remove="handleRemove3" :on-preview="handlePreview3" :file-list="fileList3" list-type="picture">
                <el-button size="small" type="primary">{{$t('shopBuildForm.clickUpload')}}</el-button>
                <div slot="tip" class="el-upload__tip">{{$t('shopBuildForm.uploadImageSizeFor5000KB')}}</div>
              </el-upload>
            </el-form-item>
            <el-form-item :label="$t('shopBuildDetail.confirmPhoto')" prop="confirmPhoto" v-if="processStatus != '产品经理待确认'">
              <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/门店建设" :on-preview="handlePreview3" :file-list="fileList3" list-type="picture">
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :disabled="submitDisabled"  @click="formSubmit()">{{$t('shopBuildForm.save')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script>
  import dictEnumSelect from 'components/basic/dict-enum-select';
  import accountSelect from 'components/basic/account-select';
  import depotSelect from 'components/future/depot-select';
  export default{
    components:{
      dictEnumSelect,
      accountSelect,
      depotSelect
    },
    data(){
      return this.getData();
    },
    methods:{
      getData(){
        return {
          isCreate:this.$route.query.id==null,
          submitDisabled: false,
          shopDisabled:false,
          fileList1: [],
          fileList2:[],
          fileList3:[],
          fixtureContent:'',
          processStatus:'',
          inputForm: {
            extra:{}
          },
          recentSaleDescription:'',
          rules: {
            shopId: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            investInCause: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            shopType: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            fixtureType: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            newContents: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            buildType: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            applyAccountId: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            scenePhoto: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
            shopAgreement: [{required: true, message: this.$t('shopBuildForm.prerequisiteMessage')}],
          },
          remoteLoading:false,
        }
      },
      formSubmit(){
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        this.inputForm.scenePhoto = util.getFolderFileIdStr(this.fileList1);
        this.inputForm.shopAgreement = util.getFolderFileIdStr(this.fileList2);
        this.inputForm.confirmPhoto = util.getFolderFileIdStr(this.fileList3);
        if(this.processStatus === "产品经理待确认"){
            if(util.isBlank(this.inputForm.confirmPhoto)){
                this.$alert("请上传装修后照片");
                this.submitDisabled = false;
                return;
            }
        }
        form.validate((valid) => {
          if (valid) {
            axios.post('/api/ws/future/layout/shopBuild/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
              this.$message(response.data.message);
              if(response.data.success) {
                if (this.isCreate) {
                  Object.assign(this.$data,this.getData());
                  this.initPage();
                }
                else{
                  util.closeAndBackToPage(this.$router,'shopBuildList')
                }
              }
            }).catch(() => {
              this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },shopChange(){
        axios.get('/api/basic/sys/dictEnum/findByValue',{params: {value:this.inputForm.fixtureType,category:'装修类别'}}).then((response)=>{
          this.fixtureContent=response.data;
        })
      },refreshRecentMonthSaleAmount(){
        if(util.isBlank(this.inputForm.shopId)){
          this.recentSaleDescription='';
          return;
        }

        axios.get('/api/ws/future/basic/depot/getRecentMonthSaleAmount' , {params: {depotId: this.inputForm.shopId, monthQty:3}}).then((response) => {
          if(response.data){
            let tmp = '';
            for(let key in response.data){
              tmp = tmp + key +"销量："+  response.data[key] +"台；";
            }
            this.recentSaleDescription=tmp;
          }else{
            this.recentSaleDescription='';
          }
        });
      },
      handlePreview1(file) {
        window.open(file.viewUrl);
      },handleChange1(file, fileList) {
        this.fileList1 = fileList;
      },handleRemove1(file, fileList) {
        this.fileList1 = fileList;
      },handlePreview2(file) {
        window.open(file.viewUrl);
      },handleChange2(file, fileList) {
        this.fileList2 = fileList;
      },handleRemove2(file, fileList) {
        this.fileList2= fileList;
      },handlePreview3(file) {
        window.open(file.viewUrl);
      },handleChange3(file, fileList) {
        this.fileList3 = fileList;
      },handleRemove3(file, fileList) {
        this.fileList3= fileList;
      },initPage(){
        axios.get('/api/ws/future/layout/shopBuild/getForm').then((response)=>{
          this.inputForm = response.data;
          if(!this.isCreate) {
            axios.get('/api/ws/future/layout/shopBuild/findOne', {params: {id: this.$route.query.id}}).then((response) => {
                this.processStatus = response.data.processStatus;
              util.copyValue(response.data, this.inputForm);
              this.shopDisabled = true;
              this.refreshRecentMonthSaleAmount();
              if (this.inputForm.fixtureType != null) {
                this.shopChange();
              }
              if (this.inputForm.scenePhoto != null) {
                axios.get('/api/general/sys/folderFile/findByIds', {params: {ids: this.inputForm.scenePhoto}}).then((response) => {
                  this.fileList1 = response.data;
                });
              }
              if (this.inputForm.shopAgreement != null) {
                axios.get('/api/general/sys/folderFile/findByIds', {params: {ids: this.inputForm.shopAgreement}}).then((response) => {
                  this.fileList2 = response.data;
                });
              }
              if (this.inputForm.confirmPhoto != null) {
                axios.get('/api/general/sys/folderFile/findByIds', {params: {ids: this.inputForm.confirmPhoto}}).then((response) => {
                  this.fileList3 = response.data;
                });
              }
            });
          }
        });
      }
    },created () {
      this.initPage();
    }
  }
</script>
