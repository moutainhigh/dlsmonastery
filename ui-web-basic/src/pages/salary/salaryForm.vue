<template>
  <div>
    <head-tab active="salaryForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px" class="form input-form">
        <el-form-item :label="$t('salaryForm.exportTemplate')" prop="id">
          <el-radio-group v-model="inputForm.salaryTemplateId">
            <el-radio v-for="item in inputForm.extra.salaryTemplates" :key="item.id" :label="item.id" class="inline-radio">{{item.name}}<a  class="download" @click="onLoad(item.id)">下载</a></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('salaryForm.exportData')" prop="folderFileId">
          <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/salary" multiple    :on-change="handleChange" :on-remove="handleRemove" :on-preview="handlePreview" :file-list="fileList" list-type="picture">
            <el-button size="small" type="primary">{{$t('salaryForm.clickUpload')}}</el-button>
            <div class="el-upload__tip" slot="tip">{{$t('salaryForm.uploadImageSizeFor5000KB')}}</div>
          </el-upload>
        </el-form-item>
          <el-form-item :label="$t('salaryForm.yearMonth')" prop="yearMonth">
            <el-date-picker v-model="inputForm.month"  type="month" :placeholder="$t('salaryForm.selectMonth')"></el-date-picker>
          </el-form-item>
        <el-form-item :label="$t('salaryForm.remarks')" prop="remarks">
          <el-input v-model="inputForm.remarks"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()">{{$t('dictEnumForm.save')}}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>


  export default{
    data(){
      return this.getData()
    },
    methods:{
      getData() {
        return{
          isCreate:this.$route.query.id==null,
          submitDisabled:false,
          fileList:[],
          inputForm:{
            extra:{}
          },
          rules: {

          }
        }
      },
      formSubmit(){
        var that=this;
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            this.inputForm.folderFileId=util.getFolderFileIdStr(this.fileList);
            this.inputForm.month=util.formatLocalMonth(this.inputForm.month)
            axios.post('/api/basic/hr/salary/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
              this.$message(response.data.message);
              if(this.isCreate){
                Object.assign(this.$data, this.getData());
                this.initPage();
              }else{
                  util.closeAndBackToPage(this.$router,"salaryList")
              }
            }).catch(function () {
              that.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },initPage(){
        axios.get('/api/basic/hr/salary/getForm').then((response)=>{
          this.inputForm = response.data;
        });
      },onLoad(id){
        window.location.href="/api/basic/hr/salaryTemplate/export?id="+id;
      },handlePreview(file) {
        window.open(file.url);
      },handleChange(file, fileList) {
        this.fileList = fileList;
      },handleRemove(file, fileList) {
        this.fileList = fileList;
      }
    },created () {
      this.initPage();
    }
  }
</script>
<style>
  .download{
    color:blue;
    margin-left: 6px
  }
  .inline-radio{
    display: block;
  }
  .el-radio.inline-radio{
    margin-bottom: 20px;
  }
  .el-radio+.el-radio{
    margin-left: 0 !important;
  }
</style>
