<template>
  <div>
    <head-tab active="shopAttributeForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px" class="form input-form">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="门店名称" prop="depotId">
              <span>{{inputForm.shop.name}}</span>
            </el-form-item>
            <el-form-item label="运营商社会渠道" prop="channelType">
              <dict-map-select v-model="inputForm.shop.channelType"  category="门店_渠道类型" ></dict-map-select>
            </el-form-item>
            <el-form-item label="地区属性" prop="areaType">
              <dict-map-select v-model="inputForm.shop.areaType"  category="门店_地区属性" ></dict-map-select>
            </el-form-item>
            <el-form-item label="地区" prop="districtId">
              <district-select v-model="inputForm.shop.districtId"></district-select>
            </el-form-item>
            <el-form-item label="月OPPO销量" prop="shopMonthTotal">
              <dict-enum-select v-model="inputForm.shopMonthTotal" category="店月总量"></dict-enum-select>
            </el-form-item>
            <el-form-item label="门店任务量" prop="taskQty">
              <el-input v-model="inputForm.shop.taskQty"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item v-for="item in inputForm.shopAttributeDetailList" :key="item.typeName" :label="item.typeName" prop="taskQty">
              <el-input v-model="item.typeValue"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()">{{$t('accountForm.save')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
  import dictEnumSelect from 'components/basic/dict-enum-select'
  import dictMapSelect from 'components/basic/dict-map-select'
  import districtSelect from 'components/general/district-select'
  export default{
    components:{
      dictEnumSelect,
      dictMapSelect,
      districtSelect
    },
    data(){
      return this.getData()
    },
    methods:{
      getData() {
      return{
        isInit:false,
        isCreate:this.$route.query.shopId==null,
        multiple:true,
        submitDisabled:false,
        formProperty:{positionDtoList:[]},
        employees:[],
        leaders:[],
        offices:[],
        depots:[],
        dataScopeOffices:[],
        inputForm:{
          shop:{},
          shopAttributeDetailList:{},
          shopMonthTotal:""
        },
        submitData:{
          shop:{},
          shopAttributeDetailList:{},
          shopMonthTotal:""
        },
        rules: {
          employeeId: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
          loginName: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
          officeId: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
          positionId: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
        },
        remoteLoading:false
      }
    },
      formSubmit(){
        var that = this;
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            util.copyValue(this.inputForm, this.submitData);
            axios.post('/api/ws/future/layout/shopAttribute/save',qs.stringify(this.submitData, {allowDots:true})).then((response)=> {
              this.$message(response.data.message);
            Object.assign(this.$data, this.getData());
              if(!this.isCreate){
                util.closeAndBackToPage(this.$router,'shopAttributeList')
              }
            }).catch(function () {
              that.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      }
    },activated () {
      if(!this.$route.query.headClick || !this.isInit) {
        Object.assign(this.$data, this.getData());
        axios.get('/api/ws/future/layout/shopAttribute/getForm',{params: {shopId:this.$route.query.shopId}}).then((response)=>{
          this.inputForm = response.data;
        console.log(this.inputForm)
      });
      }
      this.isInit = true;
    }
  }
</script>
