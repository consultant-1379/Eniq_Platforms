Imports System.Runtime.Serialization

Public Class InvalidBOVersionException
    Inherits System.Exception

    Public Sub New()
        Me.New("An error has occurred.", Nothing)
    End Sub

    Public Sub New(ByVal message As String)
        Me.New(message, Nothing)
    End Sub

    Public Sub New(ByVal message As String, ByVal innerException As Exception)
        MyBase.New(message, innerException)
        Source = "TPIDE_BOIntf"
    End Sub

    Protected Sub New(ByVal info As SerializationInfo, ByVal context As StreamingContext)
        MyBase.New(info, context)
    End Sub

End Class
