// https://umijs.org/config/
import { defineConfig } from 'umi';
import defaultSettings from './defaultSettings';
import proxy from './proxy';

const { REACT_APP_ENV } = process.env;
export default defineConfig({
  hash: true,
  antd: {},
  dva: {
    hmr: true,
  },
  locale: {
    // default zh-CN
    default: 'zh-CN',
    // default true, when it is true, will use `navigator.language` overwrite default
    antd: true,
    baseNavigator: true,
  },
  dynamicImport: {
    loading: '@/components/PageLoading/index',
  },
  targets: {
    ie: 11,
  },
  // umi routes: https://umijs.org/docs/routing
  routes: [
    {
      path: '/user',
      component: '../layouts/UserLayout',
      routes: [
        {
          name: 'login',
          path: '/user/login',
          component: './user/login',
        },
      ],
    },
    {
      path: '/',
      component: '../layouts/SecurityLayout',
      routes: [
        {
          path: '/',
          component: '../layouts/BasicLayout',
          authority: ['ADMIN', 'USER'],
          routes: [
            {
              path: '/',
              redirect: '/welcome',
            },
            {
              path: '/welcome',
              name: 'welcome',
              icon: 'home',
              component: './Welcome',
            },
            {
              path: '/display',
              name: 'display',
              icon: 'dashboard',
              component: './display',
            },
            {
              path: '/log',
              name: 'log',
              icon: 'profile',
              authority: ['USER'],
              component: './log',
            },
            {
              path: '/admin',
              name: 'admin',
              icon: 'user',
              component: './userManage',
              authority: ['ADMIN'],
              routes: [
                // {
                //   path: '/admin/sub-page',
                //   name: 'sub-page',
                //   icon: 'smile',
                //   component: './Welcome',
                //   authority: ['admin'],
                // },
              ],
            },
            {
              name: 'list.table-list',
              icon: 'table',
              path: '/list',
              component: './ListTableList',
            },
            {
              component: './404',
            },
          ],
        },
        {
          component: './404',
        },
      ],
    },
    {
      component: './404',
    },
  ],
  // Theme for antd: https://ant.design/docs/react/customize-theme-cn
  theme: {
    // ...darkTheme,
    'primary-color': defaultSettings.primaryColor,
    'menu-dark-bg': '#2A3F54',
    'menu-dark-submenu-bg': '#2A3F54',
    'menu-dark-popup-bg':'#2A3F54',
    'layout-sider-background':'#2A3F54',
    'layout-header-background':'#EDEDED'
  },
  // @ts-ignore
  title: false,
  ignoreMomentLocale: true,
  // proxy: proxy[REACT_APP_ENV || 'dev'],
  proxy: {
    '/api/': {
      target: 'http://121.40.87.226:9000/',
      changeOrigin: true,
      pathRewrite: {
        '^/api': '',
      },
    },
  },
  manifest: {
    basePath: '/',
  },
  externals: {
    AMap: 'AMap',
    Loca: 'Loca'
    // BMapLib:'BMapLib'
  },
  scripts: [
    // 'http://api.map.baidu.com/api?ak=OWB4sxHnYQfCyiwxxdNvM9CDzsyDBX40',
    // 'http://api.map.baidu.com/library/Heatmap/2.0/src/Heatmap_min.js'
    'https://webapi.amap.com/maps?v=1.4.15&key=7e23601269dc69a706d1c8c1a7783247',
    'https://webapi.amap.com/loca?v=1.3.2&key=7e23601269dc69a706d1c8c1a7783247'
  ]
});
