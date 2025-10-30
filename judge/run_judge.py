import argparse, subprocess, tempfile, os, sys

def run_with_limits(cmd, input_data, timeout=2, cwd=None):
    try:
        p = subprocess.run(cmd, input=input_data.encode(), capture_output=True, timeout=timeout, cwd=cwd)
        return p.returncode, p.stdout.decode(), p.stderr.decode()
    except subprocess.TimeoutExpired:
        return 124, '', 'TLE'

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--language', required=True)
    ap.add_argument('--code', required=True)
    ap.add_argument('--input', required=True)
    ap.add_argument('--expected', required=True)
    args = ap.parse_args()

    with tempfile.TemporaryDirectory() as td:
        if args.language.lower() in ['python','py']:
            src = os.path.join(td, 'main.py')
            with open(src,'w',encoding='utf-8') as f: f.write(args.code)
            rc, out, err = run_with_limits(['python','main.py'], args.input, cwd=td)
            if rc == 124:
                print('TLE')
                sys.exit(2)
            if out == args.expected:
                print('ACCEPTED')
                sys.exit(0)
            else:
                print('WRONG_ANSWER')
                print('OUTPUT:\n'+out)
                sys.exit(1)
        elif args.language.lower() == 'java':
            src = os.path.join(td, 'Main.java')
            with open(src,'w',encoding='utf-8') as f: f.write(args.code)
            rc, out, err = run_with_limits(['javac','Main.java'], '', cwd=td)
            if rc != 0:
                print('COMPILE_ERROR')
                print(err)
                sys.exit(3)
            rc, out, err = run_with_limits(['java','Main'], args.input, cwd=td)
            if rc == 124:
                print('TLE')
                sys.exit(2)
            if out == args.expected:
                print('ACCEPTED')
                sys.exit(0)
            else:
                print('WRONG_ANSWER')
                print('OUTPUT:\n'+out)
                sys.exit(1)
        elif args.language.lower() in ['cpp','c++']:
            src = os.path.join(td, 'main.cpp')
            with open(src,'w',encoding='utf-8') as f: f.write(args.code)
            rc, out, err = run_with_limits(['g++','-O2','-std=c++17','main.cpp','-o','main'], '', cwd=td)
            if rc != 0:
                print('COMPILE_ERROR')
                print(err)
                sys.exit(3)
            rc, out, err = run_with_limits(['./main'], args.input, cwd=td)
            if rc == 124:
                print('TLE')
                sys.exit(2)
            if out == args.expected:
                print('ACCEPTED')
                sys.exit(0)
            else:
                print('WRONG_ANSWER')
                print('OUTPUT:\n'+out)
                sys.exit(1)
        else:
            print('UNKNOWN_LANGUAGE')
            sys.exit(4)

if __name__ == '__main__':
    main()
