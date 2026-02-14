import { createI18n } from 'vue-i18n'

const messages = {
    zh: {
        language: '语言', // 用于显示当前语言
        login: {
            title: '后台管理系统',
            username: '用户名',
            password: '密码',
            btn: '登录',
            success: '登录成功'
        },
        student: {
            title: '学生列表',
            add: '添加学生',
            refresh: '刷新数据',
            table: {
                name: '姓名',
                age: '年龄',
                action: '操作',
                edit: '编辑',
                delete: '删除'
            }
        }
    },
    ja: {
        language: '言語',
        login: {
            title: '管理システム',
            username: 'ユーザー名',
            password: 'パスワード',
            btn: 'ログイン',
            success: 'ログインしました'
        },
        student: {
            title: '学生リスト',
            add: '追加',
            refresh: '更新',
            table: {
                name: '氏名',
                age: '年齢',
                action: '操作',
                edit: '編集',
                delete: '削除'
            }
        }
    },
    en: { // 🆕 新增英语
        language: 'Language',
        login: {
            title: 'Admin System',
            username: 'Username',
            password: 'Password',
            btn: 'Sign In',
            success: 'Login Successful'
        },
        student: {
            title: 'Student List',
            add: 'Add Student',
            refresh: 'Refresh',
            table: {
                name: 'Name',
                age: 'Age',
                action: 'Action',
                edit: 'Edit',
                delete: 'Delete'
            }
        }
    }
}

const i18n = createI18n({
    legacy: false,
    locale: localStorage.getItem('lang') || 'zh', // 优先从缓存读，没有就默认中文
    fallbackLocale: 'zh',
    messages
})

export default i18n