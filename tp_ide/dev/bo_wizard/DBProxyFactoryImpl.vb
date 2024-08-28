' Concrete implementation of DBProxyFactory interface
'
'
Public Class DBProxyFactoryImpl
    Implements DBProxyFactory

    '' 
    'WI 2.10
    ' Protected creator function to get DatabaseProxy.
    ' @return A new DBProxy object
    Public Function createDatabaseProxy() As DBProxy Implements DBProxyFactory.createDatabaseProxy
        Return New DatabaseProxy()
    End Function

End Class
