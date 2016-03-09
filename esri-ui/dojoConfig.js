var locationPath = location.pathname.replace(/\/[^\/]+$/, '');

window.dojoConfig = {
  async: true,
  parseOnLoad: true,
  deps: ['app/main'],

  packages: [

  {
    name: 'app',
    location: locationPath + 'app',
    main: 'main'
  },

 
  {
    name: 'react',
    location: locationPath + 'bower_components/react/',
    main: 'react'
  }, 
  
   {
    name: 'react-router',
    location:'https://npmcdn.com/react-router/umd/',
    main: 'ReactRouter.min'
  },

  {
    name: 'redux',
    location:'https://cdnjs.cloudflare.com/ajax/libs/redux/3.3.1/',
    main: 'redux.min'
  },

  {
    name: 'react-redux',
    location:'https://cdnjs.cloudflare.com/ajax/libs/react-redux/4.4.0/',
    main: 'react-redux.min'
  },

  {
    name: 'redux-thunk',
    location:'https://npmcdn.com/redux-thunk@2.0.1/dist/',
    main: 'redux-thunk.min'
  },

  {
    name: 'history',
    location:'https://npmcdn.com/history/umd/',
    main: 'History.min'
  },

  {
    name: 'i18next',
    location: locationPath + 'bower_components/i18next/',
    main: 'i18next.min'
  },

  {
    name: 'i18next-xhr-backend',
    location: locationPath + 'bower_components/i18next-xhr-backend/',
    main: 'i18nextXHRBackend.min'
  },

  {
    name: 'axios',
    location: locationPath + 'bower_components/axios/dist',
    main: 'axios.min'
  },

  {
    name: 'babel-polyfill',
    location: locationPath + 'bower_components/babel-polyfill',
    main: 'browser-polyfill'
  },

  {
    name: 'es6-promise',
    location: locationPath + 'bower_components/es6-promise',
    main: 'es6-promise.min'
  },

  {
    name: 'react-redux-router',
    location: locationPath + 'ext/react-redux-router',
    main: 'index'
  },

  ]



};
