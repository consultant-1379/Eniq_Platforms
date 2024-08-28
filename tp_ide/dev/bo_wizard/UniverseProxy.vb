Imports Designer

''
'Proxy for accessing Designer universe.
''
Public Class UniverseProxy
    Implements IUniverseProxy

    Private m_universe As Designer.IUniverse

    Public Sub New()
        ' default constructor
    End Sub

    Public Sub New(ByRef universeRef As Designer.IUniverse)
        Me.m_universe = universeRef
    End Sub


    Public Property Universe() As IUniverse Implements IUniverseProxy.Universe
        Get
            Universe = m_universe
        End Get

        Set(ByVal Value As IUniverse)
            m_universe = Value
        End Set

    End Property

    ''
    ' Adds one context to universe. If context already exists, it is selected. 
    '
    ' @param Univ Specifies reference to universe
    ' @param ContextName Name of the context
    ' @return Reference to context

    Public Function addContext(ByRef ContextName As String) As Designer.Context Implements IUniverseProxy.addContext

        Dim Conxt As Designer.Context
        Try
            Conxt = m_universe.Contexts.Item(ContextName)
        Catch ex As Exception
            Conxt = m_universe.Contexts.Add(ContextName)
        Finally
            'Do nothing            
        End Try
        Return Conxt
    End Function

    Public Function getContexts() As Designer.Contexts Implements IUniverseProxy.getContexts
        Return m_universe.Contexts
    End Function

    Public Function getJoins() As Designer.IJoins Implements IUniverseProxy.getJoins
        Dim joins As Designer.IJoins
        joins = m_universe.Joins
        Return joins
    End Function

    Public Function getJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin Implements IUniverseProxy.getJoin
        Dim join As Designer.IJoin
        join = joins.Item(joinExpression)
        Return join
    End Function

    Public Function addJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin Implements IUniverseProxy.addJoin
        Dim join As Designer.IJoin
        join = joins.Add(joinExpression)
        Return join
    End Function

    Public Function addPredefinedCondition(ByVal designerClass As Designer.IClass, ByVal conditionName As String) As Designer.PredefinedCondition Implements IUniverseProxy.addPredefinedCondition
        Return designerClass.PredefinedConditions.Add(conditionName, designerClass)
    End Function

    Public Function getPredefinedCondition(ByVal designerClass As Designer.IClass, ByVal conditionName As String) As Designer.PredefinedCondition Implements IUniverseProxy.getPredefinedCondition
        Return designerClass.PredefinedConditions(conditionName)
    End Function

    Public Function getObject(ByRef Cls As Designer.IClass, ByVal objectName As String) As Designer.IObject Implements IUniverseProxy.getObject
        Dim universeObject As Designer.IObject = Nothing
        universeObject = Cls.Objects(objectName)
        Return universeObject
    End Function

    Public Function addObject(ByVal objectName As String, ByRef Cls As Designer.IClass) As Designer.IObject Implements IUniverseProxy.addObject
        Dim universeObject As Designer.IObject = Nothing
        universeObject = Cls.Objects.Add(objectName, Cls)
        Return universeObject
    End Function

    Public Function getClass(ByRef classname As String) As Designer.IClass Implements IUniverseProxy.getClass
        Dim Cls As Designer.IClass = Nothing
        Try
            Cls = m_universe.Classes.FindClass(classname)
        Catch e As Exception
            Trace.WriteLine("Class '" & classname & "' is not found. Add class to TP Definition.")
            Trace.WriteLine("Class Exception: " & e.ToString)
        End Try
        Return Cls
    End Function

    ''
    ' Sets default formatting for universe object. 
    '
    ' @param Obj Specifies reference to object
    ' @return Formatting mask
    Public Sub formatObject(ByRef Obj As Designer.IObject) Implements IUniverseProxy.formatObject
        Dim format As String = ""
        If Obj.Type = Designer.DsObjectType.dsNumericObject Then
            format = "0;-0;0"
        Else
            ' If not a numeric object, return empty string:
            format = ""
        End If
        Obj.Format.NumberFormat = format
    End Sub

    Public Sub addToObjectsTables(ByRef Obj As Designer.IObject, ByVal tableName As String) Implements IUniverseProxy.addToObjectsTables
        Obj.Tables.Add(tableName)
    End Sub

End Class
