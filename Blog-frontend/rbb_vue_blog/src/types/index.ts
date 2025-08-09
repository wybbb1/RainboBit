// src/types/index.ts

/**
 * 文章实体接口
 */
export interface Article {
  /** 文章ID */
  id: number | string;
  /** 标题 */
  title: string;
  /** 文章内容 */
  content: string;
  /** 文章摘要 */
  summary?: string;
  /** 所属分类id */
  categoryId: number | string;
  /** 分类名称 */
  categoryName: string;
  /** 标签/**
 * 文件上传响应
 */
}

export interface UploadResponse {
  /** 响应码 */
  code: string;
  /** 响应消息 */
  msg: string;
  /** 文件URL */
  data: string;
}

export interface Article {
  /** 文章ID */
  id: number | string;
  /** 标题 */
  title: string;
  /** 文章内容 */
  content: string;
  /** 文章摘要 */
  summary?: string;
  /** 所属分类id */
  categoryId: number | string;
  /** 分类名称 */
  categoryName: string;
  /** 标签ID列表 */
  tagIds: number[];
  /** 缩略图 */
  thumbnail?: string;
  /** 是否置顶（0否，1是） */
  isTop?: string;
  /** 状态（0已发布，1草稿） */
  status?: string;
  /** 访问量 */
  viewCount: number;
  /** 是否允许评论（1是，0否） */
  isComment?: string;
  /** 创建人ID */
  createBy?: number;
  /** 创建时间 */
  createTime: string;
  /** 更新人ID */
  updateBy?: number;
  /** 更新时间 */
  updateTime?: string;
  /** 删除标志（0代表未删除，1代表已删除） */
  delFlag?: number;
}

/**
 * 分类实体接口
 */
export interface Category {
  /** 分类ID */
  id: number;
  /** 分类名 */
  name: string;
  /** 父分类id，如果没有父分类为-1 */
  pid: number;
  /** 描述 */
  description: string;
  /** 状态（0:正常,1禁用） */
  status: string;
  /** 创建人ID */
  createBy: number;
  /** 创建时间 */
  createTime: string;
  /** 更新人ID */
  updateBy: number;
  /** 更新时间 */
  updateTime: string;
  /** 删除标志（0代表未删除，1代表已删除） */
  delFlag: number;
  /** 引用次数 */
  refer_cnt: number;
}

/**
 * 评论实体接口
 */
export interface Comment {
  /** 评论ID */
  id: string | number;
  /** 评论类型（0代表文章评论，1代表友链评论） */
  type?: string;
  /** 文章id */
  articleId: string | number;
  /** 根评论id */
  rootId: string | number;
  /** 评论内容 */
  content: string;
  /** 所回复的目标评论的userid */
  toCommentUserId: string | number;
  /** 所回复的目标评论的用户名 */
  toCommentUserName?: string | null;
  /** 回复目标评论id */
  toCommentId: string | number;
  /** 创建人ID */
  createBy: string | number;
  /** 创建时间 */
  createTime: string;
  /** 更新人ID */
  updateBy?: number;
  /** 更新时间 */
  updateTime?: string;
  /** 删除标志（0代表未删除，1代表已删除） */
  delFlag?: number;
  /** 用户名 */
  username?: string;
  /** 用户头像 */
  avatar?: string;
  /** 评论子结构 */
  children?: Comment[] | null;
}

/**
 * 友链实体接口
 */
export interface Link {
  /** 友链ID */
  id: number;
  /** 友链名称 */
  name: string;
  /** 友链logo */
  logo: string;
  /** 友链描述 */
  description: string;
  /** 网站地址 */
  address: string;
  /** 审核状态（0代表审核通过，1代表审核未通过，2代表未审核） */
  status: string;
  /** 创建人ID */
  createBy: number;
  /** 创建时间 */
  createTime: string;
  /** 更新人ID */
  updateBy: number;
  /** 更新时间 */
  updateTime: string;
  /** 删除标志（0代表未删除，1代表已删除） */
  delFlag: number;
}

/**
 * 登录用户实体接口
 */
export interface LoginUser {
  /** 用户信息 */
  user: User;
  /** 权限列表 */
  permissions: string[];
}

/**
 * 菜单实体接口
 */
export interface Menu {
  /** 菜单ID */
  id: number;
  /** 菜单名称 */
  menuName: string;
  /** 父菜单ID */
  parentId: number;
  /** 显示顺序 */
  orderNum: number;
  /** 路由地址 */
  path: string;
  /** 组件路径 */
  component: string;
  /** 是否为外链（0是 1否） */
  isFrame: number;
  /** 菜单类型（M目录 C菜单 F按钮） */
  menuType: string;
  /** 菜单状态（0显示 1隐藏） */
  visible: string;
  /** 菜单状态（0正常 1停用） */
  status: string;
  /** 权限标识 */
  perms: string;
  /** 菜单图标 */
  icon: string;
  /** 创建者 */
  createBy: number;
  /** 创建时间 */
  createTime: string;
  /** 更新者 */
  updateBy: number;
  /** 更新时间 */
  updateTime: string;
  /** 备注 */
  remark: string;
  /** 删除标志 */
  delFlag: string;
  /** 子菜单列表 */
  children?: Menu[];
}

/**
 * 角色实体接口
 */
export interface Role {
  /** 角色ID */
  id: number;
  /** 角色名称 */
  roleName: string;
  /** 角色权限字符串 */
  roleKey: string;
  /** 显示顺序 */
  roleSort: number;
  /** 角色状态（0正常 1停用） */
  status: string;
  /** 删除标志（0代表存在 1代表删除） */
  delFlag: string;
  /** 创建者 */
  createBy: number;
  /** 创建时间 */
  createTime: string;
  /** 更新者 */
  updateBy: number;
  /** 更新时间 */
  updateTime: string;
  /** 备注 */
  remark: string;
}

/**
 * 标签实体接口
 */
export interface Tag {
  /** 标签ID */
  id: number;
  /** 标签名 */
  name: string;
  /** 创建人ID */
  createBy: number;
  /** 创建时间 */
  createTime: string;
  /** 更新人ID */
  updateBy: number;
  /** 更新时间 */
  updateTime: string;
  /** 删除标志（0代表未删除，1代表已删除） */
  delFlag: number;
  /** 备注 */
  remark: string;
}

/**
 * 用户实体接口
 */
export interface User {
  /** 用户ID */
  id: number;
  /** 用户名 */
  userName: string;
  /** 昵称 */
  nickName: string;
  /** 密码 */
  password: string;
  /** 用户类型：0代表普通用户，1代表管理员 */
  type: string;
  /** 账号状态（0正常 1停用） */
  status: string;
  /** 邮箱 */
  email: string;
  /** 手机号 */
  phonenumber: string;
  /** 用户性别（0男，1女，2未知） */
  sex: string;
  /** 头像URL */
  avatar: string;
  /** 创建人的用户ID */
  createBy: number;
  /** 创建时间 */
  createTime: string;
  /** 更新人ID */
  updateBy: number;
  /** 更新时间 */
  updateTime: string;
  /** 删除标志（0代表未删除，1代表已删除） */
  delFlag: number;
}

/**
 * 用户登录DTO
 */
export interface UserLoginDTO {
  /** 用户名 */
  userName: string;
  /** 密码 */
  password: string;
}

/**
 * 用户注册DTO
 */
export interface UserRegisterDTO {
  /** 用户名 */
  userName: string;
  /** 昵称 */
  nickName: string;
  /** 密码 */
  password: string;
  /** 邮箱 */
  email: string;
  /** 手机号 */
  phonenumber?: string;
  /** 用户性别（0男，1女，2未知） */
  sex?: string;
}

/**
 * 登录响应数据
 */
export interface LoginResponse {
  /** 访问令牌 */
  token: string;
}

/**
 * 上传文件响应
 */
export interface UploadResponse {
  /** 响应码 */
  code: string;
  /** 响应消息 */
  msg: string;
  /** 文件URL */
  data: string;
}
