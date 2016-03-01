import React from 'react';
import {Navbar,Nav,NavDropdown,MenuItem,NavBrand,NavItem}  from 'react-bootstrap';
import { Link  } from 'react-router';
import Message from './Message.jsx';
import * as Actions from '../actions/Actions.es6';
import Constants from '../constants/Contants.es6';
import LanStore from '../stores/LanStore.es6';

export default class Header extends React.Component {

  constructor() {
    super();
    this.state = LanStore.get();
  }

  changeLan(evt){
    let lan=evt.target.value;
    Actions.invoke(Constants.ACTION_CHANGE_LANGUAGE,lan);
  }

  componentDidMount() {
    this.unsubscribe = LanStore.listen(this.changeLanguage.bind(this));
  }

  componentWillUnmount() {
    this.unsubscribe();
  }

  changeLanguage(lan){
    this.setState(lan)
  }


  render() {
    console.log(this.state.lan)
    return (
      <div className="header">
        <nav className="navbar navbar-default">
          <div className="container-fluid">
            <div className="navbar-header">
              <a className='navbar-brand' href='http://www.neda.gov.ph/'>
                <img src='images/neda-logo-white.png' className='logo'/>                    
              </a>
            </div>
            <div className="nav navbar-rigth lan-selector-container">
              <select  value={this.state.lan} name="lan" className="pull-right" onChange={this.changeLan}>
                <option value="en">{Message.t('header.language.english')}</option>
                <option value="es">{Message.t('header.language.spanish')}</option>
              </select>
            </div>
          </div>
        </nav>
      </div>
    )
  }
}
