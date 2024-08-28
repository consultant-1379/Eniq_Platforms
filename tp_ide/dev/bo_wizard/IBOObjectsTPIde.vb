Public Interface IBOObjectsTPIde

    Sub removeObjectsForEBS(ByRef Cls As Designer.IClass, ByRef cnts As CountersTPIde)

    Function addObject(ByRef Cls As Designer.Class, ByRef cnt As CountersTPIde.Counter, ByRef cnt_name As String, ByRef Counter As Boolean, _
                       ByRef AggrFunc As String) As Designer.Object

    Function addObject(ByRef univ_class As String, ByRef univ_object As String, ByRef objType As String, ByRef objSelect As String, _
                       ByRef description As String) As Boolean

    Function getObjectsFromDatabase(ByRef tp_name As String, ByRef tp_release As String, ByRef conn As System.Data.Odbc.OdbcConnection, _
                               ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                               ByRef mts As MeasurementTypesTPIde, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, _
                               ByRef UniverseNameExtension As String, ByRef TechPackTPIde As String, ByVal addToUniverse As Boolean) As Boolean

    Function addBusyHourRankObjects(ByRef mts As MeasurementTypesTPIde) As Boolean

    Property ObjectParse() As Boolean

End Interface
