import React from 'react';
import { connect } from 'react-redux'
import {loadProjects} from 'app/actions/map'
import * as Constants from 'app/constants/constants';

class Component extends React.Component {
	
	componentDidMount(){
		this.props.onLoadProjects('region');
	}

	onChangeLevel(e){
		
		this.props.onLoadProjects(e.target.value);
	}

	render(){
		return (<div>
			<select name='level' onChange={this.onChangeLevel.bind(this)}>
			<option value='region'>Region</option>
			<option value='department'>Department</option>
			<option value='municipalities'>Municipalities</option>
			</select>
			</div>)
	}
}




const stateToProps = (state, props) => {
  return state.map  
}


const dispatchToProps = (dispatch, ownProps) => {
  return {
    onLoadProjects: (level) => {
      dispatch(loadProjects(level));
    },
  }
}
/*Connect map component to redux state*/
const LayerControl=connect(stateToProps,dispatchToProps)(Component);


export {LayerControl};
 