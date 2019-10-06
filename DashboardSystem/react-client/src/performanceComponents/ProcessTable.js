import React, { Component, Fragment } from 'react';
import { Table, Col, Row, Container } from 'react-bootstrap';

//process tale component

class ProcessTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
       processList: []
    }
  }

  componentDidMount() {
    console.log("process did mount");
  }

  componentWillUnmount() {
    console.log("process unmount");
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.tableData !== nextProps.tableData) {
      //this.props.tableData.map((message, i) => i===0 && this.messageArrived(message));
      this.messageArrived(this.props.tableData);
      console.log("receive props");
    }
  }

  messageArrived(data) {
    let dataArray = data.split(';');
    let cols = dataArray[0];
    const temp = [];

    for(let i=0; i<cols; i++) {
      temp.push({ pid: dataArray[4*i+1], cpu: dataArray[4*i+2], mem: dataArray[4*i+3], name: dataArray[4*i+4]});
    }
    this.setState({
      processList: temp
    });
    //console.log(this.state.processList);
  }

  renderTableData() {
    return this.state.processList.map((process, index) => {
         return (
           <tr key={index}>
                <td>{process.pid}</td>
                <td>{process.cpu}</td>
                <td>{process.mem}</td>
                <td>{process.name}</td>
           </tr>
         )
    })
  }

  render() {
    const tableStyle = {
      width:'44vw',
      height:'42vh',
      float:'left',
      overflow:'auto',
      display:'inline-block',
      background:'white',
      borderRadius: '25px',
      padding:'2%',
      margin:'1%',
      fontSize:'0.9rem',
      position:'absolute',
      top:'45vh',
      left:'47vw'
    };

    return (
      <Fragment>

        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        <div style={tableStyle}>
          <Table striped bordered hover size="sm">
            <thead>
              <tr>
                <th>PID</th>
                <th>CPU</th>
                <th>Memory</th>
                <th>Name</th>
              </tr>
            </thead>
            <tbody>
              {this.renderTableData()}
            </tbody>
          </Table>
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default ProcessTable;
