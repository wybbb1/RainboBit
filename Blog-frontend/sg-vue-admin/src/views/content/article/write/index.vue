<template>
  <div class="app-container">

    <el-form ref="form" :model="form" label-width="90px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="文章标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入文章标题"
              maxlength="30"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="分类">
            <el-select v-model="form.categoryId" placeholder="请选择">
              <el-option
                v-for="category in categoryList"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
              <!-- <el-option :key="'1'" label="女" :value="'1'" /> -->
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="标签">
            <el-select v-model="form.tagIds" placeholder="请选择" multiple>
              <el-option
                v-for="tag in tagList"
                :key="tag.id"
                :label="tag.name"
                :value="tag.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="文章摘要">
            <el-input v-model="form.summary" type="textarea" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="允许评论">
            <el-radio-group v-model="form.isComment">
              <el-radio :key="'0'" :label="'0'">正常</el-radio>
              <el-radio :key="'1'" :label="'1'">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="是否置顶">
            <el-radio-group v-model="form.isTop">
              <el-radio :key="'0'" :label="'0'">是</el-radio>
              <el-radio :key="'1'" :label="'1'">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

      </el-row>
      <el-row :gutter="20" />

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="缩略图">
            <el-upload
              :file-list="fileList"
              class="upload-demo"
              list-type="picture"
              drag
              name="img"
              action="upload"
              :on-remove="fileRemove"
              :limit="1"
              :http-request="handleUpload"
              :on-exceed="onExceed"
            >
              <i class="el-icon-upload" />
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <el-button type="primary" size="medium" @click="handleSubmit">{{ aId?"更新":"发布" }}</el-button>
          </el-form-item>
          <el-form-item>
            <el-button v-if="!aId" type="info" @click="handleSave">保存到草稿箱</el-button>
          </el-form-item>

        </el-col>
      </el-row>
      <el-row>
        <mavon-editor ref="md" v-model="form.content" @imgAdd="addImg" />
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { listAllCategory } from '@/api/content/category'
import { uploadImg } from '@/api/content/upload'
import { addArticle, getArticle, updateArticle } from '@/api/content/article'
import { listAllTag } from '@/api/content/tag'
export default {
  name: 'Write',
  data() {
    return {
      form: {
        title: '',
        thumbnail: '',
        isTop: '1',
        isComment: '0',
        content: '',
        tagIds: []
      },
      categoryList: [],
      tagList: [],
      aId: -1,
      fileList: []
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.aId = route.query && route.query.id
        console.log(this.aId)
      },
      immediate: true
    }
  },
  created() {
    this.getCategoryAndTag()
    if (this.aId) {
      this.getArticle()
    }
  },
  methods: {
    getArticle() {
      getArticle(this.aId).then(response => {
        console.log('获取文章数据:', response)
        this.form = response
        // 确保 tagIds 是数组格式
        if (response.tagIds && Array.isArray(response.tagIds)) {
          this.form.tagIds = response.tagIds
          console.log('设置 tagIds:', this.form.tagIds)
        } else {
          this.form.tagIds = []
          console.log('tagIds 为空，设置为空数组')
        }
        // 移除可能存在的 tags 字段，避免冗余
        if (this.form.tags) {
          console.log('移除 tags 字段:', this.form.tags)
          delete this.form.tags
        }
        if (response.thumbnail) {
          this.fileList.push({ name: '缩略图', url: response.thumbnail })
        }
      })
    },
    handleSave() {
      // 确保数据格式正确
      const submitData = { ...this.form }
      submitData.status = '1'
      // 移除可能存在的 tags 字段
      if (submitData.tags) {
        delete submitData.tags
      }
      addArticle(submitData).then(response => {
        this.$modal.msgSuccess('保存草稿成功')
      })
    },
    handleSubmit() {
      // 确保数据格式正确
      const submitData = { ...this.form }
      // 移除可能存在的 tags 字段
      if (submitData.tags) {
        console.log('提交前移除 tags 字段:', submitData.tags)
        delete submitData.tags
      }

      console.log('准备提交的数据:', submitData)
      console.log('tagIds:', submitData.tagIds)

      if (!this.aId) {
        submitData.status = '0'
        addArticle(submitData).then(response => {
          this.$modal.msgSuccess('博客发布成功')
          this.$router.push({ path: '/content/article' })
        })
      } else {
        // 更新博客信息
        updateArticle(submitData).then(response => {
          this.$modal.msgSuccess('博客更新成功')
          this.$router.push({ path: '/content/article' })
        })
      }
    },
    onExceed() {
      this.$message.error('只能上传一个图片')
    },
    handleUpload(img) {
      uploadImg(img.file).then(response => {
        this.form.thumbnail = response
        this.fileList.push({ name: img.file.name, url: response })
      }).catch(error => {
        this.$message.error(error.msg)
      })
    },
    fileRemove(file, fileList) {
      this.fileList.pop()
    },
    // 绑定@imgAdd event
    addImg(pos, file) {
      // 第一步.将图片上传到服务器.
      uploadImg(file).then(response => {
        this.$refs.md.$img2Url(pos, response)
      }).catch(error => {
        this.$message.error(error.msg)
      })
    },
    getCategoryAndTag() {
      listAllCategory().then((response) => {
        this.categoryList = response.data || response.rows || response || []
        console.log('获取到的分类列表:', this.categoryList)
      })
      listAllTag().then(response => {
        this.tagList = response.data || response.rows || response || []
        console.log('获取到的标签列表:', this.tagList)
      })
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }
  }
}
</script>
<style scoped>
.el-col .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
