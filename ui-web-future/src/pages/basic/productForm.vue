<template>
  <div>
    <head-tab active="productForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px"  class="form input-form">
        <el-row>
          <el-col :span="10">
            <el-form-item :label="$t('productForm.name')">{{inputForm.name}}
            </el-form-item>
            <el-form-item :label="$t('productForm.code')" prop="code">{{inputForm.code}}
            </el-form-item>
            <el-form-item :label="$t('productForm.outGroupName')" prop="outGroupName">{{inputForm.outGroupName}}
            </el-form-item>
            <el-form-item :label="$t('productForm.productTypeName')" prop="productTypeName">
              <product-type-select v-model="inputForm.productTypeId"></product-type-select>
            </el-form-item>
            <el-form-item  :label="$t('productForm.netType')" prop="netType">
              <el-select v-model="inputForm.netType"   clearable  :placeholder="$t('productForm.select')">
                <el-option v-for="netType in inputForm.extra.netTypeList" :key="netType" :label="netType" :value="netType"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('productForm.hasIme')" prop="hasIme">
              <bool-radio-group v-model="inputForm.hasIme"></bool-radio-group>
            </el-form-item>
            <el-form-item :label="$t('productForm.allowOrder')" prop="allowOrder">
              <bool-radio-group v-model="inputForm.allowOrder"></bool-radio-group>
            </el-form-item>
            <el-form-item :label="$t('productForm.visible')" prop="visible">
              <bool-radio-group v-model="inputForm.visible"></bool-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item :label="$t('productForm.price2')" prop="price2">
              <el-input v-model="inputForm.price2"></el-input>
            </el-form-item>
            <el-form-item :label="$t('productForm.retailPrice')" prop="retailPrice">
              <el-input v-model="inputForm.retailPrice"></el-input>
            </el-form-item>
            <el-form-item  :label="$t('productForm.depositPrice')" prop="depositPrice">
              <el-input v-model="inputForm.depositPrice"></el-input>
            </el-form-item>
            <el-form-item :label="$t('productForm.mappingName')" prop="mappingName">
              <el-input v-model="inputForm.mappingName"></el-input>
            </el-form-item>
            <el-form-item :label="$t('productForm.image')" prop="image">
              <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/货品管理" :on-change="handleChange" :on-remove="handleRemove" :on-preview="handlePreview" :file-list="fileList" list-type="picture">
                <el-button size="small" type="primary">{{$t('shopBuildForm.clickUpload')}}</el-button>
                <div slot="tip" class="el-upload__tip">{{$t('shopBuildForm.uploadImageSizeFor5000KB')}}</div>
              </el-upload>
            </el-form-item>
            <el-form-item :label="$t('productForm.remarks')" prop="remarks">
              <el-input v-model="inputForm.remarks"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="formSubmit()">{{$t('productForm.save')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
  import productTypeSelect from 'components/future/product-type-select'
  import boolRadioGroup from 'components/common/bool-radio-group'
  export default{
    components:{
      productTypeSelect,
      boolRadioGroup
    },
    data(){
      return this.getData()
    },
    methods:{
      getData() {
      return{
        isInit:false,
        isCreate:this.$route.query.id==null,
        submitDisabled:false,
        fileList: [],
        inputForm:{
            extra:{}
        },
        rules: {
        },
        remoteLoading:false,
      }
    },
      formSubmit(){
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          this.inputForm.image = util.getFolderFileIdStr(this.fileList);
          if (valid) {
            axios.post('/api/ws/future/basic/product/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
              this.$message(response.data.message);
              if(!this.isCreate){
                util.closeAndBackToPage(this.$router,'productList')
              }else{
                Object.assign(this.$data, this.getData());
                this.initPage();
              }
            }).catch(()=> {
              this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },handlePreview(file) {
        window.open(file.url);
      },handleChange(file, fileList) {
        this.fileList = fileList;
      },handleRemove(file, fileList) {
        this.fileList = fileList;
      },initPage(){
        axios.get('/api/ws/future/basic/product/getForm').then((response)=>{
          this.inputForm = response.data;
          if(!this.isCreate){
            axios.get('/api/ws/future/basic/product/findOne',{params: {id:this.$route.query.id}}).then((response)=>{
              util.copyValue(response.data,this.inputForm);
              if(response.data.productType != null){
                this.productTypeList = response.data.productType;
              }
              if (this.inputForm.image != null) {
                axios.get('/api/general/sys/folderFile/findByIds', {params: {ids: this.inputForm.image}}).then((response) => {
                  this.fileList = response.data;
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
