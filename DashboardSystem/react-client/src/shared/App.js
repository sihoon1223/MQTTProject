import React, { Component } from 'react';
import { Connector } from 'mqtt-react';
import { BrowserRouter as Router, HashRouter, Route, Switch } from 'react-router-dom';
import { Perform, Traffic, Subscription } from '../pages';
import Menu from '../components/Menu';
import Sidebar from '../components/Sidebar';


class App extends Component {  
    /*constructor(props) {
      super(props);
      this.start = {nodeResponse: ""};
    }

    callAPI() {
        fetch("http://localhost:8088/users")
            .then(res => res.text())
            .then(res => this.setState({nodeResponse: res}));
    }

    componentWillMount() {
        this.callAPI();
    }

    */

    render() {
        return (
          <Router>
            <div>
              <Menu />
              <Sidebar/>
                <Route exact path="/" component={Perform} />
                <Route path="/traffic" component={Traffic} />
                <Route path="/subscription" component={Subscription} />
            </div>
          </Router>
        );
    }
}

export default () => (
    <Connector mqttProps="ws://113.198.85.240:9001">
        <App />
    </Connector>
);
