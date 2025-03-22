<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="queryParams.password"
          placeholder="请输入密码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          placeholder="请输入用户昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="queryParams.email"
          placeholder="请输入邮箱"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input
          v-model="queryParams.realName"
          placeholder="请输入真实姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号码" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入身份证号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证正面图片路径" prop="idCardFrontImg">
        <el-input
          v-model="queryParams.idCardFrontImg"
          placeholder="请输入身份证正面图片路径"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证反面图片路径" prop="idCardBackImg">
        <el-input
          v-model="queryParams.idCardBackImg"
          placeholder="请输入身份证反面图片路径"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户头像路径" prop="avatar">
        <el-input
          v-model="queryParams.avatar"
          placeholder="请输入用户头像路径"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="默认收款方式(1:支付宝 2:微信 3:银行卡)" prop="paymentMethod">
        <el-input
          v-model="queryParams.paymentMethod"
          placeholder="请输入默认收款方式(1:支付宝 2:微信 3:银行卡)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="绑定的收款方式(JSON格式）" prop="paymentMethodIds">
        <el-input
          v-model="queryParams.paymentMethodIds"
          placeholder="请输入绑定的收款方式(JSON格式）"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总收入" prop="totalIncome">
        <el-input
          v-model="queryParams.totalIncome"
          placeholder="请输入总收入"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总支出" prop="totalExpense">
        <el-input
          v-model="queryParams.totalExpense"
          placeholder="请输入总支出"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="钱包余额" prop="walletBalance">
        <el-input
          v-model="queryParams.walletBalance"
          placeholder="请输入钱包余额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分" prop="points">
        <el-input
          v-model="queryParams.points"
          placeholder="请输入积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="红包数量" prop="redPackets">
        <el-input
          v-model="queryParams.redPackets"
          placeholder="请输入红包数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['alse:user:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['alse:user:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['alse:user:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alse:user:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="密码" align="center" prop="password" />
      <el-table-column label="用户昵称" align="center" prop="nickname" />
      <el-table-column label="手机号" align="center" prop="phone" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="真实姓名" align="center" prop="realName" />
      <el-table-column label="身份证号码" align="center" prop="idCardNo" />
      <el-table-column label="身份证正面图片路径" align="center" prop="idCardFrontImg" />
      <el-table-column label="身份证反面图片路径" align="center" prop="idCardBackImg" />
      <el-table-column label="用户头像路径" align="center" prop="avatar" />
      <el-table-column label="默认收款方式(1:支付宝 2:微信 3:银行卡)" align="center" prop="paymentMethod" />
      <el-table-column label="默认收款账号信息" align="center" prop="paymentAccount" />
      <el-table-column label="绑定的收款方式(JSON格式）" align="center" prop="paymentMethodIds" />
      <el-table-column label="总收入" align="center" prop="totalIncome" />
      <el-table-column label="总支出" align="center" prop="totalExpense" />
      <el-table-column label="钱包余额" align="center" prop="walletBalance" />
      <el-table-column label="积分" align="center" prop="points" />
      <el-table-column label="红包数量" align="center" prop="redPackets" />
      <el-table-column label="收藏的商品" align="center" prop="favoriteProducts" />
      <el-table-column label="浏览记录" align="center" prop="browsingHistory" />
      <el-table-column label="关注的用户ID列表" align="center" prop="followedUserIds" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['alse:user:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alse:user:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改用户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号码" prop="idCardNo">
          <el-input v-model="form.idCardNo" placeholder="请输入身份证号码" />
        </el-form-item>
        <el-form-item label="身份证正面图片路径" prop="idCardFrontImg">
          <el-input v-model="form.idCardFrontImg" placeholder="请输入身份证正面图片路径" />
        </el-form-item>
        <el-form-item label="身份证反面图片路径" prop="idCardBackImg">
          <el-input v-model="form.idCardBackImg" placeholder="请输入身份证反面图片路径" />
        </el-form-item>
        <el-form-item label="用户头像路径" prop="avatar">
          <el-input v-model="form.avatar" placeholder="请输入用户头像路径" />
        </el-form-item>
        <el-form-item label="默认收款方式(1:支付宝 2:微信 3:银行卡)" prop="paymentMethod">
          <el-input v-model="form.paymentMethod" placeholder="请输入默认收款方式(1:支付宝 2:微信 3:银行卡)" />
        </el-form-item>
        <el-form-item label="默认收款账号信息" prop="paymentAccount">
          <el-input v-model="form.paymentAccount" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="绑定的收款方式(JSON格式）" prop="paymentMethodIds">
          <el-input v-model="form.paymentMethodIds" placeholder="请输入绑定的收款方式(JSON格式）" />
        </el-form-item>
        <el-form-item label="总收入" prop="totalIncome">
          <el-input v-model="form.totalIncome" placeholder="请输入总收入" />
        </el-form-item>
        <el-form-item label="总支出" prop="totalExpense">
          <el-input v-model="form.totalExpense" placeholder="请输入总支出" />
        </el-form-item>
        <el-form-item label="钱包余额" prop="walletBalance">
          <el-input v-model="form.walletBalance" placeholder="请输入钱包余额" />
        </el-form-item>
        <el-form-item label="积分" prop="points">
          <el-input v-model="form.points" placeholder="请输入积分" />
        </el-form-item>
        <el-form-item label="红包数量" prop="redPackets">
          <el-input v-model="form.redPackets" placeholder="请输入红包数量" />
        </el-form-item>
        <el-form-item label="收藏的商品" prop="favoriteProducts">
          <el-input v-model="form.favoriteProducts" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="浏览记录" prop="browsingHistory">
          <el-input v-model="form.browsingHistory" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="关注的用户ID列表" prop="followedUserIds">
          <el-input v-model="form.followedUserIds" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUser, getUser, delUser, addUser, updateUser } from "@/api/alse/user";

export default {
  name: "User",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: null,
        password: null,
        nickname: null,
        phone: null,
        email: null,
        realName: null,
        idCardNo: null,
        idCardFrontImg: null,
        idCardBackImg: null,
        avatar: null,
        paymentMethod: null,
        paymentAccount: null,
        paymentMethodIds: null,
        totalIncome: null,
        totalExpense: null,
        walletBalance: null,
        points: null,
        redPackets: null,
        favoriteProducts: null,
        browsingHistory: null,
        followedUserIds: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        username: [
          { required: true, message: "用户名不能为空", trigger: "blur" }
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      listUser(this.queryParams).then(response => {
        this.userList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        userId: null,
        username: null,
        password: null,
        nickname: null,
        phone: null,
        email: null,
        realName: null,
        idCardNo: null,
        idCardFrontImg: null,
        idCardBackImg: null,
        avatar: null,
        paymentMethod: null,
        paymentAccount: null,
        paymentMethodIds: null,
        totalIncome: null,
        totalExpense: null,
        walletBalance: null,
        points: null,
        redPackets: null,
        favoriteProducts: null,
        browsingHistory: null,
        followedUserIds: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加用户";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const userId = row.userId || this.ids
      getUser(userId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改用户";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userId != null) {
            updateUser(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addUser(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids;
      this.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function() {
        return delUser(userIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alse/user/export', {
        ...this.queryParams
      }, `user_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
