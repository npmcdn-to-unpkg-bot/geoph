import React from 'react';
import { Link } from 'react-router';
import Header from './header.jsx';
import Footer from './footer.jsx';
import Panel  from './panel.jsx';
import Landing  from './landing.jsx';
import Dashboard  from '../dashboard/dashboard.jsx';
import Menu from '../menu/default.jsx';

require("./root.scss");

export default class DefaultLayout extends React.Component {

  constructor() {
    super();
  }


  render() {

    return (
      <div className="root">
      <Header>
        <Menu title="Executive Dashboards"/>
      </Header>
          <Dashboard/>
           
      </div>
    )
  }
}