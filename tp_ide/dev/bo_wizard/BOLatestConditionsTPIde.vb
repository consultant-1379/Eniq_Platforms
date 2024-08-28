Imports System.Collections

''
'Adds conditions to the universe for selecting Latest N Hours and Latest N Days.
''
Public Class BOLatestConditionsTPIde

    ''
    '@param universeProxy       A proxy for accessing the universe.
    '@param Cls                 The class the condition will be added to.
    '@param tableType           One of RAW, COUNT, DELTA or DAY.
    '@param field               Either DATETIME_ID or DATE_ID depending on the table type.
    '@param bo_conditions       Reference to BOConditionsTPIde to parse the condition.
    '@param measType            Reference to the measurement type.
    '@returns conditionAdded    True if the condition was added ok.
    Public Function addLatestNHoursOrDaysCondition(ByRef universeProxy As IUniverseProxy, ByVal Cls As Designer.IClass, _
                                ByVal tableType As String, ByVal field As String, ByVal bo_conditions As BOConditionsTPIde, _
                                ByVal measType As MeasurementTypesTPIde.MeasurementType) As Boolean
        Dim conditionAdded As Boolean = True

        Try
            ' Get the time interval (either "day" or hour/minute depending on the table type: 
            Dim timeInterval As String = getTimeInterval(tableType)

            ' Add condition to universe:
            Dim Cond As Designer.PredefinedCondition = addConditionToUniverse(timeInterval, tableType, Cls, universeProxy)
            Cond.Description = getConditionDescription(timeInterval, measType.TypeName & "_" & tableType)

            ' Get the table name that will be used:
            Dim tableName As String = "DC." & measType.TypeName & "_" & tableType
            ' Get the table name and field:
            Dim tableNameAndField As String = "DC." & measType.TypeName & "_" & tableType & "." & field
            ' Set the where statement:
            Dim whereStatement As String = getWhereStatement(timeInterval, tableNameAndField, tableName)
            Cond.Where = whereStatement

            ' Check that the condition parses ok:
            If bo_conditions.ParseCondition(Cond, Cls) = False Then
                conditionAdded = False
            End If
        Catch ex As Exception
            Trace.WriteLine("Error adding latest N hour/day condition: " & ex.ToString)
            conditionAdded = False
        End Try        
        Return conditionAdded
    End Function

    ''
    'Gets the time interval: either hour or day.
    '@param     tableType       One of RAW, COUNT, DELTA or DAY.
    '@returns   timeInterval    Either "hour" or "day".
    Protected Function getTimeInterval(ByVal tableType As String) As String        
        Dim timeInterval As String = ""
        If (tableType = "RAW") OrElse (tableType = "COUNT") OrElse (tableType = "DELTA") Then
            timeInterval = "minute"
        ElseIf (tableType = "DAY") OrElse (tableType = "DAYBH") Then
            timeInterval = "day"
        Else
            ' Throw an exception if the table type is not recognised:
            Throw New Exception("Error adding Latest N Hours/Days conditions: failed to get time interval for table type: " & tableType)
        End If
        Return timeInterval
    End Function

    ''
    'Adds a condition to the universe. Checks if it's there already, if not it adds it as a new condition.
    '@param timeInterval    Either "minute" or "day".
    '@param tableType       One of RAW, COUNT, DELTA or DAY.
    '@param Cls             The class the condition will be added to.
    '@param universeProxy   A proxy for accessing the universe.
    '@returns Cond          A reference to the condition that has been added.
    Protected Function addConditionToUniverse(ByVal timeInterval As String, ByVal tableType As String, ByVal Cls As Designer.IClass, _
                                       ByVal universeProxy As IUniverseProxy) As Designer.PredefinedCondition
        ' Define reference to condition:
        Dim Cond As Designer.PredefinedCondition
        Dim conditionName As String = getConditionName(timeInterval, tableType)
        ' Get the condition from the universe. If it doesn't exist, add it in:
        Try
            Cond = universeProxy.getPredefinedCondition(Cls, conditionName)
            UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
        Catch e As Exception
            Cond = universeProxy.addPredefinedCondition(Cls, conditionName)
            UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
        End Try
        Trace.WriteLine("BOLatestConditionsTPIde, addConditionToUniverse(): Added condition: " & Cls.Name & "/" & Cond.Name & " to universe")
        Return Cond
    End Function

    ''
    'Gets the 'where' statement for the condition.
    '@param     timeInterval        Either "minute" or "day".
    '@param     tableNameAndField   The table name and field: e.g. DC_E_SGSN_SGSN_COUNT.DATETIME_ID
    '@param     tableName           The table name: e.g. DC_E_SGSN_SGSN_COUNT
    '@returns   whereStatement      The 'where' statement for the condition.
    Public Function getWhereStatement(ByVal timeInterval As String, ByVal tableNameAndField As String, ByVal tableName As String) As String
        Dim prompt As String = getPrompt(timeInterval)
        Dim whereStatement As String = tableNameAndField & " <= ( SELECT MAX(" & tableNameAndField & ") FROM " & tableName & ")"
        whereStatement &= " AND " & tableNameAndField & " >= (SELECT DATEADD(" & timeInterval & ", -" & prompt _
        & " , MAX(" & tableNameAndField & "))" & " FROM " & tableName & ")" _
        & " AND " & tableNameAndField & " >= (SELECT DATEADD(" & timeInterval & ", -"

        If (timeInterval = "day") Then
            whereStatement &= "31"
        ElseIf (timeInterval = "minute") Then
            whereStatement &= "1440"
        End If

        whereStatement &= " , MAX(" & tableNameAndField & "))" & " FROM " & tableName & ")"

        Return whereStatement
    End Function

    ''
    'Gets the prompt that will be used in the 'where' statement.
    '@param     timeInterval    Either "minute" or "day".
    '@returns   prompt          The prompt string.
    '@remarks   The 'Latest' prompt is the 'from' date, the earliest date we need to get data from.
    Protected Function getPrompt(ByVal timeInterval As String) As String
        Dim prompt As String = ""
        If (timeInterval = "minute") Then
            ' 15 minute periods for a day:
            prompt = "@Prompt('Lookback period (minutes):','N',{'0', '15', '30', '45', '60', '75', '90', '105', '120', '135', '150', '165', '180', " _
            & "'195', '210', '225', '240', '255', '270', '285', '300', '315', '330', '345','360', '375', '390', '405', '420', '435', '450', '465', " _
            & "'480', '495', '510', '525', '540', '555', '570', '585', '600', '615', '630', '645', '660', '675', '690', '705', '720', '735', '750', " _
            & "'765', '780', '795', '810', '825', '840', '855', '870', '885', '900', '915', '930', '945', '960', '975', '990', '1005', '1020', '1035', " _
            & "'1050', '1065', '1080', '1095', '1110', '1125', '1140', '1155', '1170', '1185', '1200', '1215', '1230', '1245', '1260', '1275', '1290', " _
            & "'1305', '1320', '1335', '1350', '1365', '1380', '1395', '1410', '1425', '1440'},mono,constrained)"
        ElseIf (timeInterval = "day") Then
            prompt = "@Prompt('Lookback period (days):','N','Alarm Time Range\Day Range',mono,constrained)"
        End If
        Return prompt
    End Function

    ''
    'Gets the condition name.
    '@param     timeInterval    Either "hour" or "day".
    '@param     tableType       One of RAW, COUNT, DELTA or DAY.
    '@returns   conditionName   The condition's name.
    Protected Function getConditionName(ByVal timeInterval As String, ByVal tableType As String) As String
        Dim conditionName As String = ""
        conditionName = "Select lookback period (" & tableType & ")"
        Return conditionName
    End Function

    ''
    'Gets the condition description.
    '@param     timeInterval            Either "hour" or "day".
    '@param     tableName               The table name that is the source for the data.
    '@returns   conditionDescription    The condition's description.
    Protected Function getConditionDescription(ByVal timeInterval As String, ByVal tableName As String) As String
        Dim conditionDescription As String = ""
        If (timeInterval = "hour") Then
            conditionDescription = "Latest N Hours before the latest event time. Takes data from " & tableName & " table."
        ElseIf (timeInterval = "day") Then
            conditionDescription = "Latest N Days before latest event time. Takes data from " & tableName & " table."
        End If
        Return conditionDescription
    End Function

End Class
