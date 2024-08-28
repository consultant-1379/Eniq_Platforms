Imports System.Collections

Public Interface ITPUtilitiesTPIde

    Function getRankMeasurementTypes(ByVal mts As MeasurementTypesTPIde) As ArrayList

    Function getMeasurementTypeByName(ByVal mtNameToLookFor As String, ByRef mts As MeasurementTypesTPIde) As MeasurementTypesTPIde.MeasurementType

    Function setupDesignerApp(ByVal BoVersion As String, ByVal boUser As String, ByVal boPass As String, ByVal boRep As String, _
                               ByVal BoAut As String) As Designer.IApplication

    Function promptToOpenUniverse(ByRef UniverseNameExtension As String, ByVal UniverseExtension As String, ByVal BoVersion As String, _
                                   ByRef DesignerApp As Designer.IApplication, ByVal UniverseName As String, _
                                   ByVal UniverseFileName As String, ByVal outputFolder As String) As Designer.IUniverse

    Function displayMessageBox(ByVal message As String, ByVal msgBoxStyle As MsgBoxStyle, ByVal msgBoxTitle As String) As MsgBoxResult

    Function getBHTargetTypes(ByVal techpackVersion As String, ByVal rankMeasType As MeasurementTypesTPIde.MeasurementType, _
                                     ByRef tpConn As System.Data.Odbc.OdbcConnection) As ArrayList

End Interface
