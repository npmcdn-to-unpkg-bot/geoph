import * as Constants from '../constants/constants';
import {cloneDeep} from '../util/filterUtil';

const projectSearch = (state = {'selected': [], 'results': {}}, action) => {
  let projectSearchResults;
  let stateCloned;
  switch (action.type) {
    case Constants.TOGGLE_PROJECT_SELECTION:
      stateCloned = cloneDeep(state);
      let itemIndex = -1
      stateCloned.selected.forEach((item, index) => {
        if (item.id == action.project.id){
          itemIndex = index;
        }
      })
      if (itemIndex!=-1){
        stateCloned.selected.splice(itemIndex, 1);
      } else {
        stateCloned.selected.push(action.project);
      }
      return stateCloned;
    case Constants.SELECT_ALL_MATCHED_PROJECT:
      stateCloned = cloneDeep(state);
      stateCloned.results.content.forEach((item) => {
        stateCloned.selected.push(item);
      })
      return stateCloned;
    case Constants.CLEAR_ALL_PROJECT_SELECTED:
      stateCloned = cloneDeep(state);
      Object.assign(stateCloned, {'selected':[]});
      return stateCloned;

    case Constants.CLEAR_ALL_RESULTS:
      stateCloned = cloneDeep(state);
      Object.assign(stateCloned, {'results': {}});
      return stateCloned;

    case Constants.REQUEST_PROJECT_BY_TEXT:
      projectSearchResults = {isFetching: true};
      return Object.assign({}, state, {'results': projectSearchResults});
    case Constants.RECEIVE_PROJECT_BY_TEXT:
      projectSearchResults = {lastUpdate: action.receivedAt, isFetching: false};
      Object.assign(projectSearchResults, action.data);
      return Object.assign({}, state, {'results': projectSearchResults});
    default:
      return state
  }
}


export default projectSearch