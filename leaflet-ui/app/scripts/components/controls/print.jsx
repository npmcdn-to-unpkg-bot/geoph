import React from 'react';
import { connect } from 'react-redux'
import translate from '../../util/translate.js';
import {capture}  from '../../actions/capture.js';

require('./print.scss');

const Share =React.createClass({

  componentDidMount() {
        
  },

  componentWillReceiveProps(nextProps) {
      
  
  },

  render() {
   const {onCapure,loading,captures,visible}=this.props;
   return (


     <div>
     
        {visible?
          <div className="print-container">

              <div className="new_loading">{loading?<img src="../../../assets/png/loading.gif"/>:
              <div  className="new"><div className="icon" onClick={onCapure}></div><span>create</span></div>}</div>
              <span c className={captures.length>0?"small":"big"}>Click on create icon to generate a pdf of current map</span>
              {captures.map((file,index)=><a target="_blank" href={`http://localhost:8090/export/download/${file}`}><div className="icon"></div><span>{"Pdf# "+(index+1)}</span></a>)}
              
            </div>

        : null}
      </div>
    );
  }
});

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    onCapure:_=>dispatch(capture())
  }
}

const mapStateToProps = (state, props) => {

  return {...state.print.toJS()}
}

export default connect(mapStateToProps, mapDispatchToProps)(Share);
