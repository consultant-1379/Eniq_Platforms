Imports Designer

''
'Proxy for accessing a Designer universe.
''
Public Interface IUniverseProxy

    ' Joins
    Function getJoins() As Designer.IJoins

    Function getJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin

    Function addJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin

    ' Conditions
    Function addPredefinedCondition(ByVal designerClass As Designer.IClass, ByVal conditionName As String) As Designer.PredefinedCondition

    Function getPredefinedCondition(ByVal designerClass As Designer.IClass, ByVal conditionName As String) As Designer.PredefinedCondition

    ' Objects
    Function getObject(ByRef Cls As Designer.IClass, ByVal objectName As String) As Designer.IObject

    Function addObject(ByVal objectName As String, ByRef Cls As Designer.IClass) As Designer.IObject

    Sub formatObject(ByRef Obj As Designer.IObject)

    Sub addToObjectsTables(ByRef Obj As Designer.IObject, ByVal tableName As String)

    Function getClass(ByRef classname As String) As Designer.IClass

    'Contexts:
    Function addContext(ByRef ContextName As String) As Designer.Context

    Function getContexts() As Designer.Contexts

    'Properties:
    Property Universe() As IUniverse

End Interface
