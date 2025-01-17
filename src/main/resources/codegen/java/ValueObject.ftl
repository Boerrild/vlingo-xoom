package ${packageName};

public class ${valueObjectName} {

  <#list members as member>
  ${member}
  </#list>

  public static ${valueObjectName} of(${constructorParameters}) {
    return new ${valueObjectName}(${constructorInvocationParameters});
  }

  private ${valueObjectName} (${constructorParameters}) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
  }

}