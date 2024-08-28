''
'Updates a universe.
'
Public Class UpdateUnvOp
    Inherits AbstractOperation

    Private ebsTechPack As Boolean = False

    Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String, _
                   ByVal ebsTechPack As Boolean)        
        MyBase.New(operationName, bouser, bopass, borep, tpident, cmTechPack, baseident, outputFolder, eniqConn, boVersion, boAut, universe, domain)
        Me.ebsTechPack = ebsTechPack
    End Sub

    Public Overrides Function cleanup() As Boolean
        MyBase.universeFunctions.cleanup()
        MyBase.universeFunctions = Nothing
    End Function

    Public Overrides Function doOperation() As Boolean
        Dim success As Boolean = False
        Trace.WriteLine("Starting universe update: EBS tech pack = " & Me.ebsTechPack)
        success = MyBase.universeFunctions.UpdateUniverse(m_boUser, m_bopass, m_borep, m_tpident, m_cmTechPack, ebsTechPack, m_baseident, _
                                                      m_outputFolder, m_eniqConn, m_boVersion, m_boAut)
        Return success
    End Function
End Class
