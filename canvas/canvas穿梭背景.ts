/* eslint-disable @typescript-eslint/explicit-member-accessibility */
import * as React from 'react';
import { useRef, useEffect, useCallback } from 'react';
import './index.scss';

const CanvasBg: React.FC<any> = (props: any) => {
  const canvasRef = useRef<any>();
  const g = [
    [87, 171, 255],
    [242, 224, 136],
    [255, 189, 122],
    [122, 235, 141],
    [166, 148, 255],
  ];

  const init = useCallback(() => {
    const c = canvasRef.current.getContext('2d');
    const canvas = canvasRef.current;
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    const speed = 0.0024;
    const stars: any = [];

    class Star {
      x: number;
      y: number;
      px: any;
      py: any;
      z: number;
      d: number[];
      constructor() {
        this.x = Math.random() * canvas.width - canvas.width / 2;
        this.y = Math.random() * canvas.height - canvas.height / 2;
        this.px = 0;
        this.py = 0;
        this.z = 10;
        this.d = 0.75 > Math.random() ? [245, 249, 252] : g[Math.floor(Math.random() * g.length)];
      }

      update() {
        this.px = this.x;
        this.py = this.y;
        this.z += 0.2;
        this.x += this.x * this.z * speed * 0.05;
        this.y += this.y * this.z * speed * 0.05;
        if (
          this.x > canvas.width / 2 + 50 ||
          this.x < -canvas.width / 2 - 50 ||
          this.y > canvas.height / 2 + 50 ||
          this.y < -canvas.height / 2 - 50
        ) {
          this.x = Math.random() * canvas.width - canvas.width / 2;
          this.y = Math.random() * canvas.height - canvas.height / 2;
          this.px = this.x;
          this.py = this.y;
          this.z = 10;
        }
      }

      show() {
        c.lineWidth = this.z * 0.3;
        c.lineCap = 'round';
        c.beginPath();
        c.moveTo(this.x, this.y);
        c.lineTo(this.px, this.py);
        c.strokeStyle = 'rgba('
          .concat(`${this.d[0]}`, ', ')
          .concat(`${this.d[1]}`, ', ')
          .concat(`${this.d[2]}`, ', ')
          .concat(`${0.8}`, ')');
        c.stroke();
      }
    }

    for (let i = 0; i < 150; i++) stars.push(new Star());

    c.fillStyle = '#0E1525';
    c.strokeStyle =
      'rgb(' + Math.random() * 255 + ', ' + Math.random() * 255 + ', ' + Math.random() * 255 + ')';

    c.translate(canvas.width / 2, canvas.height / 2);

    function draw() {
      c.fillRect(-canvas.width / 2, -canvas.height / 2, canvas.width, canvas.height);
      for (const s of stars) {
        s.update();
        s.show();
      }
      requestAnimationFrame(draw);
    }

    draw();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    init();
    window.addEventListener('resize', init);
    return () => {
      window.removeEventListener('resize', init);
    };
  }, [init]);

  return (
    <div className="w-[100vw] h-[100vh] pageCanvas">
      <canvas ref={canvasRef} className="w-full h-full" />
    </div>
  );
};

export default CanvasBg;
