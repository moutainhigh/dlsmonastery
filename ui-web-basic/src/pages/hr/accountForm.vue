<template>
  <div>
    <head-tab active="accountForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px" class="form input-form">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item :label="$t('accountForm.employeeId')" prop="employeeId">
              <employee-select v-model="inputForm.employeeId" :disabled="!isCreate"></employee-select>
            </el-form-item>
            <el-form-item :label="$t('accountForm.loginName')" prop="loginName">
              <el-input v-model="inputForm.loginName"></el-input>
            </el-form-item>
            <el-form-item :label="$t('accountForm.password')" prop="password">
              <el-input v-model="inputForm.password"  type="password"></el-input>
            </el-form-item>
            <el-form-item :label="$t('accountForm.confirmPassword')" prop="confirmPassword">
              <el-input v-model="inputForm.confirmPassword" type="password"></el-input>
            </el-form-item>
            <el-form-item :label="$t('accountForm.depotIdList')" prop="depotIdList">
              <depot-select v-model="depotIdList" category="shop" :multiple="true"></depot-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('accountForm.officeName')" prop="officeId">
              <office-select v-model="inputForm.officeId" :disabled="!isCreate&&!hasPermit"></office-select>
            </el-form-item>
            <el-form-item :label="$t('accountForm.leader')" prop="leaderId">
              <account-select v-model="inputForm.leaderId" :disabled="!isCreate&&!hasPermit"></account-select>
            </el-form-item>
            <el-form-item :label="$t('accountForm.dataScopeOffice')" prop="officeIdList">
              <office-select v-model="inputForm.officeIdList" :multiple="true"></office-select>
            </el-form-item>
            <el-form-item :label="$t('accountForm.position')" prop="positionId">
              <el-select v-model="inputForm.positionId"  filterable :placeholder="$t('accountForm.selectGroup')" :clearable=true :disabled="!isCreate&&!hasPermit">
                <el-option v-for="position in inputForm.extra.positionDtoList" :key="position.id" :label="position.name" :value="position.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('accountForm.remarks')" prop="remarks">
              <el-input v-model="inputForm.remarks"></el-input>
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
  import employeeSelect from 'components/basic/employee-select'
  import accountSelect from 'components/basic/account-select'
  import officeSelect from 'components/basic/office-select'
  import depotSelect from 'components/future/depot-select'
  export default{
      components:{
          depotSelect,
          employeeSelect,
          accountSelect,
          officeSelect
      },
    data(){
      return this.getData();
    },
    methods:{
      getData(){
        var checkLoginName=(rule, value, callback)=>{
          if (!value) {
            return callback(new Error('必填信息'));
          }else {
            axios.get('/api/basic/hr/account/checkLoginName?loginName='+value+"&id="+this.$route.query.id).then((response)=>{
              if(response.data.success){
                return callback();
              }else {
                return callback(new Error(response.data.message));
              }
            })
          }
        };
        var validatePass = (rule, value, callback) => {
          if (value === '') {
            callback(new Error('请再次输入密码'));
          } else if (value !== this.inputForm.password&&value!==undefined) {
            callback(new Error('两次输入密码不一致!'));
          } else {
            callback();
          }
        };
        return{
          isCreate:this.$route.query.id==null,
          multiple:true,
          hasPermit:util.isPermit('hr:employee:enableUpdate'),
          submitDisabled:false,
          inputForm:{
            extra:{}
          },
          depotIdList:[],
          confirmPassword:"",
          radio:'1',
          remoteLoading:false,
          rules: {
            employeeId: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
            loginName: [{ required: true,validator: checkLoginName}],
            confirmPassword: [{ validator: validatePass }],
            officeId: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
            officeIdList: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
            positionId: [{ required: true, message: this.$t('accountForm.prerequisiteMessage')}],
          }
        }
      },
      formSubmit(){
        var that = this;
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            var submitData = util.deleteExtra(this.inputForm);
            axios.post('/api/basic/hr/account/save',qs.stringify(submitData)).then((response)=> {
            axios.post('/api/ws/future/basic/accountDepot/save', qs.stringify({accountId:response.data.extra.accountId,depotIdList:this.depotIdList}, {allowDots: true})).then((response)=> {
              this.$message(response.data.message);
              if(response.data.success){
                this.submitDisabled = false;
                if(!this.isCreate){
                    util.closeAndBackToPage(this.$router,"accountList")
                }else {
                  Object.assign(this.$data, this.getData());
                  this.initPage();
                }
              }
            })
            }).catch( ()=> {
              that.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },
      initPage(){
        if(!this.isCreate){
          axios.get('/api/ws/future/basic/accountDepot/findByAccountId',{params: {accountId:this.$route.query.id}}).then((response)=>{
            this.depotIdList=response.data;
          })
        }
        axios.get('/api/basic/hr/account/getForm',{params: {id:this.$route.query.id}}).then((response)=>{
          this.inputForm=response.data;
          axios.get('/api/basic/hr/account/findOne',{params: {id:this.$route.query.id}}).then((response)=>{
            util.copyValue(response.data,this.inputForm);
            if(this.isCreate){
              this.inputForm.type="子账号";
            }
          })
        });
      }
    },created () {
      this.initPage();
    }
  }
</script>
