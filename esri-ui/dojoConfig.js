var locationPath = location.pathname.replace(/\/[^\/]+$/, '');

window.dojoConfig = {
  async: true,
  parseOnLoad: true,
  cacheBust: true,
  deps: ['app/main'],

  packages: [

  {
    name: 'app',
    location: locationPath + 'app',
    main: 'main'
  },

 
  {
    name: 'react',
    location: locationPath + 'lib/react/',
    main: 'react'
  }, 
  
   {
    name: 'react-router',
    location:locationPath + 'lib/react-router',
    main: 'index'
  },

  {
    name: 'redux',
   location:locationPath + 'lib/redux',
    main: 'index'
  },

  {
    name: 'react-redux',
    location:locationPath + 'lib/react-redux',
    main: 'index'
  },

  {
    name: 'redux-thunk',
    location:locationPath + 'lib/redux-thunk',
    main: 'index'
  },

  {
    name: 'i18next',
    location: locationPath + 'lib/i18next/bin',
    main: 'index'
  },

  {
    name: 'i18next-xhr-backend',
    location: locationPath + 'lib/i18next-xhr-backend/bin',
    main: 'index'
  },

  {
    name: 'axios',
    location: locationPath + 'lib/axios/dist',
    main: 'axios'
  },

  {
    name: 'babel-polyfill',
    location: locationPath + 'lib/babel-polyfill',
    main: 'browser-polyfill'
  },

  {
    name: 'es6-promise',
    location: locationPath + 'lib/es6-promise',
    main: 'es6-promise.min'
  },
/*
  {
    name: 'terraformer',
    location: locationPath + 'lib/terraformer',
    main: 'terraformer.min'
  },
  {
    name: 'terraformer-arcgis-parser',
    location: locationPath + 'lib/terraformer-arcgis-parser',
    main: 'terraformer-arcgis-parser.min'
  },*/

  {
    name: 'react-redux-router',
    location: locationPath + 'ext/react-redux-router',
    main: 'index'
  },

  ]



};
