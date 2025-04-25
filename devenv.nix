{ pkgs, lib, config, inputs, ... }:

{
  # https://devenv.sh/basics/
  env.GREET = "devenv";
  name = "kotlin-algorithms-pratice";

  # https://devenv.sh/packages/
  packages = with pkgs; [ 
    git
    kotlin
    gradle
    jdk21
    ktlint
    detekt
  ];

  env = {
    JAVA_HOME = "${pkgs.jdk21}/lib/openjdk";
  };

  # https://devenv.sh/languages/
  # languages.rust.enable = true;
  languages.kotlin.enable = true;

  # https://devenv.sh/processes/
  # processes.cargo-watch.exec = "cargo-watch";
  processes.run-tests.exec = "gradle test";
  processes.lint.exec = "ktlint --relative";
  processes.static-analysis.exec = "detekt";

  # https://devenv.sh/services/
  # services.postgres.enable = true;

  # https://devenv.sh/scripts/
  enterShell = ''
    echo "🛠 Kotlin + Gradle development environment"
    echo "→ Java:    $JAVA_HOME"
    echo "→ Kotlin:  $(kotlin -version)"
    echo "→ Gradle:  $(gradle --version | head -n 1)"
    echo "→ Run 'gradle build', 'ktlint', 'detekt' inside this shell"
  '';

  # https://devenv.sh/tasks/
  # tasks = {
  #   "myproj:setup".exec = "mytool build";
  #   "devenv:enterShell".after = [ "myproj:setup" ];
  # };

  # https://devenv.sh/tests/
  enterTest = ''
    echo "Running tests"
    git --version | grep --color=auto "${pkgs.git.version}"
  '';

  # https://devenv.sh/git-hooks/
  # git-hooks.hooks.shellcheck.enable = true;

  # See full reference at https://devenv.sh/reference/options/
}
