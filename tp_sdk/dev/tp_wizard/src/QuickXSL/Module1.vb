Imports System.Xml
Imports System.Xml.Xsl

Module Module1

    Sub Main(ByVal Args() As String)
        Dim I = Args.Length
        If I <> 3 Then
            System.Console.WriteLine("Invalid number of arguments. Arguments are:")
            System.Console.WriteLine("1. XML source")
            System.Console.WriteLine("2. XSL style sheet")
            System.Console.WriteLine("3. Output file")
            Exit Sub
        End If

        System.Console.WriteLine("XML source: " & Args(0))
        System.Console.WriteLine("XSL style sheet: " & Args(1))
        System.Console.WriteLine("Output file: " & Args(2))

        Dim xslt As New XslTransform

        Try
            xslt.Load(Args(1))
            xslt.Transform(Args(0), Args(2), Nothing)
        Catch ex As Exception
            System.Console.WriteLine("Transform error: " & ex.Message & " " & ex.StackTrace)
        End Try
    End Sub

End Module
