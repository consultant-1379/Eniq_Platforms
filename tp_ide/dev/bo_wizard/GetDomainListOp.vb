''
' Lists the domains (folders) on a BO server.
' "listDomain" command.
Public Class GetDomainListOp
    Inherits AbstractOperation

    Private DesignerApp As Designer.Application

    Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String)
        MyBase.New(operationName, bouser, bopass, borep, tpident, cmTechPack, baseident, outputFolder, eniqConn, boVersion, boAut, universe, domain)
    End Sub

    Public Overrides Function cleanup() As Boolean
        DesignerApp.Quit()
        DesignerApp = Nothing
    End Function

    Public Overrides Function doOperation() As Boolean
        Dim success As Boolean = False

        Try
            DesignerApp = logOnToDesignerApp()
            If (m_boVersion = "6.5") Then
                'Dim domain As Designer.UniverseDomain()
                'For Each domain In DesignerApp.UniverseDomains
                'Console.WriteLine(domain.Name)
                'Next
            ElseIf (m_boVersion = "XI") Then
                Dim domain As Designer.UniverseFolder
                For Each domain In DesignerApp.UniverseRootFolder.Folders
                    Console.WriteLine(domain.Name)
                Next
            End If
        Catch ex As Exception
            Console.WriteLine("Error getting domain list: " & ex.ToString())
        End Try

        Return True
    End Function

    Protected Overrides Sub displayLogFileMessage()
        ' This command is used internally by IDE, so should not display a message box.
    End Sub

    Protected Overrides Sub setupTracing()
        ' This command is used internally by IDE, so should not create a log.
    End Sub
End Class
