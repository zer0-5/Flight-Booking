# Flight Reservation System

A distributed flight reservation platform designed for handling multiple client requests simultaneously with robust concurrency control. Developed as part of a project for the Distributed Systems course at Universidade do Minho.

## Features

### Core Functionality
- **Flight Reservation:** Users can reserve flights between specified locations with support for connecting flights.
- **Cancel Day:** Admins can cancel entire days. Connection flights are also cancelled.
- **Cancel Flights:** Admins can cancel flights, with automatic adjustments to related reservations.
- **User Management:** User registration, authentication, and role-based functionalities (admin and client).
- **Flight Management:** Add, view, and manage flight details.

### Additional Functionality
- **Find Routes:** Fetch all possible routes between two locations (up to two connections).
- **Password Management:** Users can update their passwords.
- **View Reservations:** Users can review all their active reservations.
- **View Notifications:** Users receive notifications about their reservations.
- **Concurrency Support:** A demultiplexer allows clients to perform multiple actions simultaneously.

## System Architecture

### Modules
1. **Client**
   - Provides a user interface for making requests to the server.
   - Implements a demultiplexer to manage multiple requests efficiently.

2. **Server**
   - Processes client requests and interfaces with the Airport System for data operations.
   - Utilizes a thread-based system for handling concurrent client interactions.

3. **Common**
   - Shared classes for data structures and utility functions.
   - Implements locks to ensure thread safety.

## Testing

Three groups of tests ensure system reliability:
1. **Airport System Logic:** Verifies correctness of individual methods and exception handling.
2. **Concurrency:** Simulates concurrent operations to test robustness and error handling.
3. **Client-Server Connection:** Validates the interaction between clients and the server under load.
