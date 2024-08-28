Imports System.Collections

Public Interface DatabaseFacadeInterface

    Function readSingleColumnFromDB(ByVal sqlStatement As String, ByVal duplicatesOk As Boolean, ByRef tpConn As System.Data.Odbc.OdbcConnection) As ArrayList

End Interface
