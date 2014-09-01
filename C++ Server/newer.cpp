/* Server

    Initialize Winsock.
    Create a socket.
    Bind the socket.
    Listen on the socket for a client.
    Accept a connection from a client.
    Receive and send data.
    Disconnect.

*/
#undef UNICODE
/* For historical reasons, the Windows.h header defaults to including the Winsock.h header file for Windows Sockets 1.1. 
The declarations in the Winsock.h header file will conflict with the declarations in the Winsock2.h header file required by Windows Sockets 2.0. 
The WIN32_LEAN_AND_MEAN macro prevents the Winsock.h from being included by the Windows.h header.*/
#define WIN32_LEAN_AND_MEAN 

//#include "stdafx.h"
#include <windows.h>
#include <winsock2.h> //provides  networking and internet for windows
#include <ws2tcpip.h>
#include <stdlib.h> 
#include <stdio.h> //standard input and output
#include <string>
#include <sstream>
#include <iostream>

using namespace std;

// Need to link with Ws2_32.lib
#pragma comment (lib, "Ws2_32.lib")
#pragma comment (lib, "Mswsock.lib") //note I uncommented this

#define DEFAULT_BUFLEN 512 //buffer length
#define DEFAULT_PORT "1060" //Port definition

int __cdecl main(void) 
{
    /*Note that All processes (applications or DLLs) that call Winsock functions must initialize the use of the Windows Sockets DLL before making other Winsock functions calls. 
    This also makes certain that Winsock is supported on the system.*/
    WSADATA wsaData; //object data to call winsock function
    int iResult;

    SOCKET ListenSocket = INVALID_SOCKET; // Generally should be zero
    SOCKET ClientSocket = INVALID_SOCKET;

    struct addrinfo *result = NULL; //addrinfo isused by the getaddrinfo (address info) (get address info)
    struct addrinfo hints;

    int iSendResult;
    char recvbuf[DEFAULT_BUFLEN];
    int recvbuflen = DEFAULT_BUFLEN;
    
    // Initialize Winsock
    iResult = WSAStartup(MAKEWORD(2,2), &wsaData); //used to call WS2_32.dll and makes a request for version 2.2 of Winsock on the system
    if (iResult != 0) {
        printf("WSAStartup failed with error: %d\n", iResult); // If error exit
        return 1;
    }
    //End Initialize Winsock
    ZeroMemory(&hints, sizeof(hints));
    hints.ai_family = AF_INET; //used to specify IPv4
    hints.ai_socktype = SOCK_STREAM; //used to specify a stream socket
    hints.ai_protocol = IPPROTO_TCP; //used to specify TCP protocol
    hints.ai_flags = AI_PASSIVE; //flag indicates the caller intends to use the returned socket address structure in a call to the bind function.

    // Resolve the server address and port
    iResult = getaddrinfo(NULL, DEFAULT_PORT, &hints, &result); //getaddrinfo determines the values in the sockaddr structure
    if ( iResult != 0 ) {
        printf("getaddrinfo failed with error: %d\n", iResult);
        WSACleanup();
        return 1;
    }

    // Create a SOCKET for connecting to server
    ListenSocket = socket(result->ai_family, result->ai_socktype, result->ai_protocol);
    if (ListenSocket == INVALID_SOCKET) {
        printf("socket failed with error: %ld\n", WSAGetLastError());
        freeaddrinfo(result);
        WSACleanup();
        return 1;
    }

    // Setup the TCP listening socket
    //sockaddr holds information regarding the address family, IP address, and port number.
    iResult = bind( ListenSocket, result->ai_addr, (int)result->ai_addrlen);
    if (iResult == SOCKET_ERROR) {
        printf("bind failed with error: %d\n", WSAGetLastError());
        freeaddrinfo(result);
        closesocket(ListenSocket);
        WSACleanup();
        return 1;
    }

    freeaddrinfo(result); //fres memory allocated by getaddrinfo

    iResult = listen(ListenSocket, SOMAXCONN);
    if (iResult == SOCKET_ERROR) {
        printf("listen failed with error: %d\n", WSAGetLastError());
        closesocket(ListenSocket);
        WSACleanup();
        return 1;
    }

    // Accept a client socket for a single connection
    ClientSocket = accept(ListenSocket, NULL, NULL);
    if (ClientSocket == INVALID_SOCKET) {
        printf("accept failed with error: %d\n", WSAGetLastError());
        closesocket(ListenSocket);
        WSACleanup();
        return 1;
    }

    
    // No longer need server socket
    closesocket(ListenSocket);

    // Receive until the peer shuts down the connection
    do {

        iResult = recv(ClientSocket, recvbuf, recvbuflen, 0);
        if (iResult > 0) {
            printf("Bytes received: %d\n", iResult);
            printf("Message Received from Client: %s\n", recvbuf);
        // Echo the buffer back to the sender
            iSendResult = send( ClientSocket, recvbuf, iResult, 0 );
            if (iSendResult == SOCKET_ERROR) {
                printf("send failed with error: %d\n", WSAGetLastError());
                closesocket(ClientSocket);
                WSACleanup();
                return 1;
            }
            printf("Bytes sent to client: %d\n", iSendResult);
            
        }
        else if (iResult == 0)
            printf("Connection closing...\n");
        else  {
            printf("recv failed with error: %d\n", WSAGetLastError());
            closesocket(ClientSocket);
            WSACleanup();
            return 1;
        }

    } while (iResult > 0);

    // shutdown the connection since we're done
    iResult = shutdown(ClientSocket, SD_SEND);
    if (iResult == SOCKET_ERROR) {
        printf("shutdown failed with error: %d\n", WSAGetLastError());
        closesocket(ClientSocket);
        WSACleanup();
        return 1;
    }

    // cleanup
    closesocket(ClientSocket);
    WSACleanup();
    system("pause");
    return 0;
    
}