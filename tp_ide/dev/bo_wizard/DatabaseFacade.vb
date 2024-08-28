Imports System.Collections

Public Class DatabaseFacade
    Implements DatabaseFacadeInterface

    ' database proxy
    Dim databaseProxy As DBProxy

    Public Sub New(ByVal databaseProxyFactory As DBProxyFactory)
        'constructor
        databaseProxy = databaseProxyFactory.createDatabaseProxy()
    End Sub

    ''
    'Read values from the database.
    '
    '@param sqlStatement
    '@param duplicatesOk
    '@param tpConn
    '@returns An ArrayList of all of the values read.
    Public Function readSingleColumnFromDB(ByVal sqlStatement As String, ByVal duplicatesOk As Boolean, _
                                           ByRef tpConn As System.Data.Odbc.OdbcConnection) As ArrayList _
                                           Implements DatabaseFacadeInterface.readSingleColumnFromDB

        Dim valueRead As String
        Dim valuesList As ArrayList
        valuesList = New ArrayList

        ' Set up database reader with SQL statement:
        Try
            databaseProxy.setupDatabaseReader(sqlStatement, tpConn)
        Catch ex As Exception
            Throw New Exception("Error setting up database reader: " & ex.ToString)
        End Try

        While (databaseProxy.read())
            If databaseProxy.getValue(0).ToString() = "" Then ' just read first value for now
                Trace.WriteLine("Found no results")
                Exit While
            Else
                valueRead = Trim(databaseProxy.getValue(0).ToString())
                If (valuesList.Contains(valueRead) = False) Then
                    valuesList.Add(valueRead)
                ElseIf (duplicatesOk = True) Then
                    ' If you want duplicates, add it to the list anyway, even if it's there already:
                    valuesList.Add(valueRead)
                End If
            End If
        End While
        Return valuesList
    End Function

End Class
