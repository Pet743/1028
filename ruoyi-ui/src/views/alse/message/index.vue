<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会话ID" prop="conversationId">
        <el-input
          v-model="queryParams.conversationId"
          placeholder="请输入会话ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发送者ID" prop="senderId">
        <el-input
          v-model="queryParams.senderId"
          placeholder="请输入发送者ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="接收者ID" prop="receiverId">
        <el-input
          v-model="queryParams.receiverId"
          placeholder="请输入接收者ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发送时间" prop="sendTime">
        <el-date-picker clearable
          v-model="queryParams.sendTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发送时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="读取时间" prop="readTime">
        <el-date-picker clearable
          v-model="queryParams.readTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择读取时间">
        </el-date-picker>
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
          v-hasPermi="['alse:message:add']"
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
          v-hasPermi="['alse:message:edit']"
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
          v-hasPermi="['alse:message:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alse:message:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="messageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="消息ID" align="center" prop="messageId" />
      <el-table-column label="会话ID" align="center" prop="conversationId" />
      <el-table-column label="发送者ID" align="center" prop="senderId" />
      <el-table-column label="接收者ID" align="center" prop="receiverId" />
      <el-table-column label="消息内容" align="center" prop="content" />
      <el-table-column label="消息类型：0-文本，1-图片，2-语音，3-视频，4-文件" align="center" prop="contentType" />
      <el-table-column label="媒体文件URL" align="center" prop="mediaUrl" />
      <el-table-column label="读取状态：0-未读，1-已读" align="center" prop="readStatus" />
      <el-table-column label="发送时间" align="center" prop="sendTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="读取时间" align="center" prop="readTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.readTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['alse:message:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alse:message:remove']"
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

    <!-- 添加或修改聊天消息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="会话ID" prop="conversationId">
          <el-input v-model="form.conversationId" placeholder="请输入会话ID" />
        </el-form-item>
        <el-form-item label="发送者ID" prop="senderId">
          <el-input v-model="form.senderId" placeholder="请输入发送者ID" />
        </el-form-item>
        <el-form-item label="接收者ID" prop="receiverId">
          <el-input v-model="form.receiverId" placeholder="请输入接收者ID" />
        </el-form-item>
        <el-form-item label="消息内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="媒体文件URL" prop="mediaUrl">
          <el-input v-model="form.mediaUrl" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发送时间" prop="sendTime">
          <el-date-picker clearable
            v-model="form.sendTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发送时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="读取时间" prop="readTime">
          <el-date-picker clearable
            v-model="form.readTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择读取时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
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
import { listMessage, getMessage, delMessage, addMessage, updateMessage } from "@/api/alse/message";

export default {
  name: "Message",
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
      // 聊天消息表格数据
      messageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        conversationId: null,
        senderId: null,
        receiverId: null,
        content: null,
        contentType: null,
        mediaUrl: null,
        readStatus: null,
        sendTime: null,
        readTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        conversationId: [
          { required: true, message: "会话ID不能为空", trigger: "blur" }
        ],
        senderId: [
          { required: true, message: "发送者ID不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "消息内容不能为空", trigger: "blur" }
        ],
        contentType: [
          { required: true, message: "消息类型：0-文本，1-图片，2-语音，3-视频，4-文件不能为空", trigger: "change" }
        ],
        readStatus: [
          { required: true, message: "读取状态：0-未读，1-已读不能为空", trigger: "change" }
        ],
        sendTime: [
          { required: true, message: "发送时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询聊天消息列表 */
    getList() {
      this.loading = true;
      listMessage(this.queryParams).then(response => {
        this.messageList = response.rows;
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
        messageId: null,
        conversationId: null,
        senderId: null,
        receiverId: null,
        content: null,
        contentType: null,
        mediaUrl: null,
        readStatus: null,
        sendTime: null,
        readTime: null,
        createTime: null,
        updateTime: null,
        createBy: null,
        updateBy: null,
        status: null,
        delFlag: null
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
      this.ids = selection.map(item => item.messageId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加聊天消息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const messageId = row.messageId || this.ids
      getMessage(messageId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改聊天消息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.messageId != null) {
            updateMessage(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMessage(this.form).then(response => {
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
      const messageIds = row.messageId || this.ids;
      this.$modal.confirm('是否确认删除聊天消息编号为"' + messageIds + '"的数据项？').then(function() {
        return delMessage(messageIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alse/message/export', {
        ...this.queryParams
      }, `message_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
