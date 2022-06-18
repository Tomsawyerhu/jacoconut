set term pdfcairo  font "simsun,12" size 12,5
set out "block-optm.pdf"

set xrange[0:12000]

set size 1,1
set origin 0,0
set multiplot

# x:block num before optimization
# y:block num after optimization
set size 0.5,1
set origin 0,0
set title "block标记数量(优化前/优化后)"
set xlabel 'block数量(优化前)'
set ylabel 'block数量(优化后)'
plot 'p1.dat'

# x:block num
# y:optm rate
set size 0.5,1
set origin 0.5,0
set title "block优化率"
set xlabel '项目规模(block数量)'
set ylabel '优化率'
plot 'p2.dat'