import { combineReducers } from 'redux';
import language from './language';
import filters from './filters';
import map from './map';
import charts from './charts';
import settings from './settings';
import security from './security';
import indicators from './indicators.js';
import popup from './popup';
import projectSearch from './projectSearch';
import saveMap from './saveMap';
import restoreMap from './restoreMap';
import stats from './stats';
import panel from './panel';
import header from './header';

import {routerReducer}  from 'react-router-redux';

/*reducer names should match with a state property*/

const geophApp = combineReducers({
  language,
  filters,
  map,
  charts,
  settings,
  indicators,
  security,
  popup,
  projectSearch,
  saveMap,
  restoreMap,
  stats,
  panel,
  header,
  routing: routerReducer
})

export default geophApp