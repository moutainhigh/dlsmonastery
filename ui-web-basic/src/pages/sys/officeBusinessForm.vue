<template>
  <div>
    <head-tab active="officeBusinessForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px" class="form input-form">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item :label="$t('officeForm.parentId')" prop="parentId">
              <office-select v-model="inputForm.parentId" ></office-select>
            </el-form-item>
            <el-form-item :label="$t('officeForm.officeName')" prop="name">
              <el-input v-model="inputForm.name"></el-input>
            </el-form-item>
            <el-form-item label="所有数据权限" prop="allDataScope" >
              <bool-radio-group v-model="inputForm.allDataScope"></bool-radio-group>
            </el-form-item>
            <el-form-item label="部门管理人" prop="leaderIdList">
              <account-select v-model="inputForm.leaderIdList" :multiple="true"></account-select>
            </el-form-item>
            <el-form-item :label="$t('officeForm.sort')" prop="sort">
              <el-input  v-model.number="inputForm.sort"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()">{{$t('officeForm.save')}}
              </el-button>
            </el-form-item>
          </el-col>
          <el-col :span = "10" >
            <el-form-item  label="授权" prop="officeIdList">
              <el-tree
                :data="treeData"
                show-checkbox
                node-key="id"
                ref="tree"
                check-strictly
                :default-checked-keys="checked"
                :default-expanded-keys="checked"
                @check-change="handleCheckChange"
                :props="defaultProps">
              </el-tree>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
  import accountSelect from 'components/basic/account-select'
  import boolRadioGroup from 'components/common/bool-radio-group'
  import officeSelect from 'components/basic/office-select'
  export default{
    components:{
      officeSelect,
      accountSelect,
      boolRadioGroup
    },
    data:function () {
      return this.getData();
    },
    methods: {
      getData(){
        var checkSort=(rule,value,callback)=>{
            if(value==""){
                callback(new Error(this.$t('officeForm.prerequisiteMessage')))
            }else if(isNaN(value)){
                callback(new Error(this.$t('bankInForm.prerequisiteAndPositiveNumberMessage')))
            }
        }
        return {
          isCreate: this.$route.query.id == null,
          multiple:true,
          submitDisabled: false,
          inputForm: {
              extra:{}
          },
          rules: {
            name: [{required: true, message: this.$t('officeForm.prerequisiteMessage')}],
            sort:[{ required: true, validator:checkSort}],
          },
          remoteLoading: false,
          treeData:[],
          checked:[],
          defaultProps: {
            label: 'label',
            children: 'children'
          }
        };
      },
      formSubmit(){
        var that=this;
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            this.inputForm.type='职能部门'
            axios.post('/api/basic/sys/office/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response) => {
              if(response.data.success){
                this.$message(response.data.message);
                if (this.isCreate) {
                  Object.assign(this.$data, this.getData());
                  this.initPage();
                }else {
                    util.closeAndBackToPage(this.$router,"officeList")
                }
              }else {
                that.submitDisabled = false;
                this.$message({
                  showClose: true,
                  message: response.data.message,
                  type: 'error'
                });
              }
            }).catch( ()=> {
              that.submitDisabled = false;
            });
          } else {
            this.submitDisabled = false;
          }
        })
      },
      handleCheckChange(data, checked, indeterminate) {
        var officeIdList=new Array()
        var check=this.$refs.tree.getCheckedKeys();
        for(var index in check){
          if(check[index]!=0){
            officeIdList.push(check[index])
          }
        }
        this.inputForm.officeIdList=officeIdList;
      },initPage(){
        axios.get('/api/basic/sys/office/getForm').then((response) => {
          this.inputForm = response.data;
          axios.get('/api/basic/sys/office/findOne', {params: {id: this.$route.query.id}}).then((response) => {
            util.copyValue(response.data,this.inputForm);
            this.$refs.tree.setCheckedKeys(response.data.businessIdList);
            this.checked=response.data.businessIdList
            this.inputForm.officeIdList = response.data.businessIdList;
          })
        })
      }
    },created () {
      axios.get('/api/basic/sys/office/getOfficeTree', {params: {id: this.inputForm.id}}).then((response) => {
        this.treeData =new Array(response.data);
      })
      this.initPage();
    }
  }
</script>
