import React from 'react';
import { connect } from 'react-redux'
import DatePicker from 'react-date-picker'
import Moment from 'moment'
import { setFilterDate, fetchFilterDataIfNeeded } from '../../actions/filters.js'


class FilterDate extends React.Component {

	constructor() {
	    super();
	    this.state = {'errorMessage': '', 'dateNow': Date.now()};
	}

	componentDidMount() {
		this.props.onLoadFilterData(this.props.filterType);		
	}

	handleStartDate(date) {
		let sd = Moment(date);
		let ed = this.refs.endDate.getDate();//.format("YYYY-MM-DD");	
		if (this.validateDates(sd, ed)){
			this.props.onDateChange({filterType: this.props.filterType, startDate:date, endDate: ed.format("YYYY-MM-DD")});
		}
	}

  	handleEndDate(date) {
		let ed = Moment(date);
		let sd = this.refs.startDate.getDate();//.format("YYYY-MM-DD");	
		if (this.validateDates(sd, ed)){
			this.props.onDateChange({filterType: this.props.filterType, startDate: ed.format("YYYY-MM-DD"), endDate: date});
		}
	}

	validateDates(startDate, endDate){
		if (startDate.isAfter(endDate) || endDate.isBefore(startDate)){
			this.setState({'errorMessage': 'End Date should be greater than Start Date'});
			return false;
		} else {
			this.setState({'errorMessage': ''});
			return true;
		}
	}

  	render() {
  		return (
	        <div className="date-picker-container">
	        	<div className="date-picker-div">
	        		<span>Start Date: <b>{this.props.startDate || "Not Set"}</b></span>
	        		<DatePicker 
	        			ref="startDate" 
	        			locale={this.props.lang} 
	        			minDate={this.props.minDate} 
	        			maxDate={this.props.endDate || this.props.maxDate} 
	        			date={this.state.dateNow} 
	        			onChange={this.handleStartDate.bind(this)} />
		        </div>	
		        <div className="date-picker-div">
	        		<span>End Date: <b>{this.props.EndDate || "Not Set"}</b></span>
	        		<DatePicker 
	        			ref="endDate" 
	        			locale={this.props.lang} 
	        			minDate={this.props.startDate || this.props.minDate} 
	        			maxDate={this.props.maxDate} 
	        			date={this.state.dateNow} 
	        			onChange={this.handleEndDate.bind(this)} />	 
		        </div>
		        {this.state.errorMessage.length>0?
					<div className="error-message">
		        		<b>Error: </b>
		        		{this.state.errorMessage}
			        </div>
		        : null}      
	        </div>
      	);
  	}
}


const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    onLoadFilterData: (type) => {
      dispatch(fetchFilterDataIfNeeded(type));
    },

    onDateChange: (filterDate) => {
      dispatch(setFilterDate(filterDate));
    }
  }
}

export default connect(null,mapDispatchToProps)(FilterDate);
