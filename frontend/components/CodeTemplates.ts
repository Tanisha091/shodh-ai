export const templates: Record<string, string> = {
  python: `# Read two integers and print their sum\n# Example input: 2 3\n# Expected output: 5\nimport sys\nprint(sum(map(int, sys.stdin.read().split())))\n`,
  java: `import java.util.*;\npublic class Main {\n  public static void main(String[] args){\n    Scanner sc = new Scanner(System.in);\n    long a = sc.nextLong(), b = sc.nextLong();\n    System.out.println(a+b);\n  }\n}\n`,
  cpp: `#include <bits/stdc++.h>\nusing namespace std;\nint main(){\n  ios::sync_with_stdio(false);cin.tie(nullptr);\n  long long a,b; if(!(cin>>a>>b)) return 0;\n  cout<<a+b<<"\n";\n}\n`,
}
