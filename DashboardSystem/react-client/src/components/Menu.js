import React from 'react';
import { Navbar, Nav, Form, Button, FormControl } from 'react-bootstrap';
import search from '../images/search.png';
import alarm from '../images/alarm.png';
import user from '../images/user.png';

// menu

const Menu = () => {
  const navbarStyle = {
    backgroundColor: 'white',
    padding: '0rem',
    //marginBottom: '2rem',
    position: 'fixed',
    top: '0',
    bottom: '0',
    left: '0',
    zIndex: '10', /* Behind the navbar */
    padding: '0 0 0', /* Height of navbar */
    height: '50px',
    width:'100%',
    overflowX: 'hidden',
    overflowY: 'hidden',
  };

  const imgStyle = {
    width: '25px',
    height:'25px'
  }
 
  return (
    <Navbar style={navbarStyle} collapseOnSelect expand="lg">
      <Navbar.Collapse id="responsive-navbar-nav">
        <Nav className="mr-auto">
        <Form inline style={{marginLeft:'55px'}}>
          <Button style={{background: 'white', borderColor:'white', marginRight:'5px'}} >
            <img alt="search" src={search} style={imgStyle}/>
          </Button>
          <FormControl type="text" placeholder="Search" className="mr-sm-2" style={{width:'600px'}}/>
        </Form>
        </Nav>
        <Nav>
          <Button style={{background: 'white', borderColor:'white', marginRight:'5px'}} >
            <img alt="alarm" src={alarm} style={imgStyle}/>
          </Button>
          <Button style={{background: 'white', borderColor:'white', marginRight:'5px'}} >
            <img alt="user" src={user} style={imgStyle}/>
          </Button>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
};

export default Menu;
