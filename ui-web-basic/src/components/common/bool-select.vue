<template>
  <div>
    <el-select v-model="innerId" clearable @change="handleChange" :disabled="disabled" >
      <el-option v-for="item in itemList"  :key="item?1:0" :label="item | bool2str" :value="item "></el-option>
    </el-select>
  </div>
</template>
<script>
  export default {
    props: ['value','disabled'],
    data() {
      return {
        innerId: this.value,
        itemList:[true, false],
      };
    },methods:{
      handleChange(newVal) {
        if(newVal !== this.value) {
          this.$emit('input', newVal);
        }
      },
      setValue(val){
        if(this.innerId === val) {
          return;
        }
        if(val === 'true' || val === true){
            this.innerId = true;
        }else if(val === 'false' || val === false){
            this.innerId = false;
        }else{
            this.innerId = null;
        }

      }
    },created () {
      this.setValue(this.value);
    },watch: {
      value :function (newVal) {
          this.setValue(newVal);
      }
    }
  };
</script>
